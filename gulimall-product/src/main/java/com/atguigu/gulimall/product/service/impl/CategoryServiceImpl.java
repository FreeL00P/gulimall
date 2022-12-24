package com.atguigu.gulimall.product.service.impl;


import cn.hutool.json.JSONUtil;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * //查出所有分类以及子分类，
     * @return 以树形结构组装
     */
    @Override
    public List<CategoryEntity> listWithTree() {
         List<CategoryEntity> entities = baseMapper.selectList(null);
        //查询一级分类
        List<CategoryEntity> level1List= entities.stream().filter((categoryEntity ->{
            return categoryEntity.getParentCid()==0;
        })).map((menu->{
            //获取子分类
            menu.setChildren(getChildrenList(menu,entities));
            return menu;
        })).sorted((menu1,menu2)->{
            //排序
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1List;
    }

    /**
     * 批量删除菜单列表
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 获取分类全路径
     * @param catelogId
     * @return
     */
    @Override
    public Long[] getCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<Long>();
        List<Long> parentPath = findParentCid(paths, catelogId);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新关联表中的数据
     * @param category
     */
//    @Caching(evict = {
//            @CacheEvict(value={"category"},key="'getLevel1Category'"),
//            @CacheEvict(value={"category"},key="'getCatalogJson'")
//    })
    @CacheEvict(value = "category",allEntries = true)//删除指定分区下的所有缓存
    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }
    @Cacheable(value="category",key ="#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        log.warn("==>在数据库中查询了catalogJSON<==");
        //将数据库查询次数变成一次
        //查询所有分类信息
        List<CategoryEntity> allCategory = baseMapper.selectList(null);
        //获取所有一级分类
        List<CategoryEntity> category1Level =getParentCid(allCategory,0L);
        Map<String, List<Catelog2Vo>> map = category1Level.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询一级分类下对应的二级分类
            List<CategoryEntity> category2Level = getParentCid(allCategory,v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (category2Level != null && category2Level.size() != 0) {
                //封装成Vo
                catelog2Vos = category2Level.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setId(item.getCatId().toString());
                    catelog2Vo.setName(item.getName());
                    catelog2Vo.setCatalog1Id(item.getParentCid().toString());
                    //查询二级分类下的三级分类
                    List<CategoryEntity> category3Level = getParentCid(allCategory,item.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3Level != null && category3Level.size() != 0) {
                        //封装成VO
                        catelog3Vos = category3Level.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
                            catelog3Vo.setCatalog2Id(level3.getParentCid().toString());
                            catelog3Vo.setId(level3.getCatId().toString());
                            catelog3Vo.setName(level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
         return map;
    }


    /**
     * 获取一级分类id
     * @return
     */
    /**
     * 每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区(按照业务类型分)】
     * 代表当前方法的结果需要缓存，如果缓存中有，方法都不用调用，如果缓存中没有，会调用方法。最后将方法的结果放入缓存
     * 默认行为
     *      如果缓存中有，方法不再调用
     *      key是默认生成的:缓存的名字::SimpleKey::[](自动生成key值)
     *      缓存的value值，默认使用jdk序列化机制，将序列化的数据存到redis中
     *      默认时间是 -1： 永久
     *
     *   自定义操作：key的生成
     *      指定生成缓存的key：key属性指定，接收一个Spel
     *      指定缓存的数据的存活时间:配置文档中修改存活时间
     *      将数据保存为json格式
     *
     *
     * 4、Spring-Cache的不足之处：
     *  1）、读模式
     *      缓存穿透：查询一个null数据。解决方案：缓存空数据
     *      缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用sync = true来解决击穿问题
     *      缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间
     *  2)、写模式：（缓存与数据库一致）
     *      1）、读写加锁。
     *      2）、引入Canal,感知到MySQL的更新去更新Redis
     *      3）、读多写多，直接去数据库查询就行
     *
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用Spring-Cache）：写模式(只要缓存的数据有过期时间就足够了)
     *      特殊数据：特殊设计
     *
     *  原理：
     *      CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
     * @return
     */
    @Cacheable(value={"category"},key="#root.method.name")  //代表当前方法的结果需要缓存 缓存中没有才会生效，将方法的返回结果放入缓存
    @Override
    public List<CategoryEntity> getLevel1Category() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>().eq("cat_level", 1);
        return this.list(wrapper);
    }

    //@Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //缓存中没有数据
            log.warn("==>缓存不命中,去数据库中查询catalogJSON<==");
            Map<String, List<Catelog2Vo>> catalogJsonForDb = getCatalogJsonForDbWithRedisLock();
            //必须将保存到缓存的代码放入同步代码块中，避免数据还没有进入缓存，就已经释放锁，
            // 其他线程就判断缓存中没有数据而造成多次查库
//            //将查询到的数据保存到redis
//            String toJsonStr = JSONUtil.toJsonStr(catalogJsonForDb);
//            redisTemplate.opsForValue().set("catalogJSON", toJsonStr,1, TimeUnit.DAYS);
            return catalogJsonForDb;
        }
        //log.warn("==>缓存命中,直接返回来catalogJSON<==");
        Map<String, List<Catelog2Vo>> map = JSONUtil.toBean(catalogJSON, Map.class);
        return map;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJSONDbWithRedissonLock(){
        RLock lock = redissonClient.getLock("CatelogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> data=null;
        try{
            data = getDataFromDb();
        }finally {
            lock.unlock();
        }
        return data;
    }

    public  Map<String, List<Catelog2Vo>> getCatalogJsonForDbWithLocalLock(){
        //TODO 本地锁synchronized，JUC(Lock)只能锁住当前进程，在分布式情况下想要锁住所有 只能使用分布式锁
        synchronized(this){
            Map<String, List<Catelog2Vo>> data = getDataFromDb();
            //将查询到的数据保存到redis
            String toJsonStr = JSONUtil.toJsonStr(data);
            redisTemplate.opsForValue().set("catalogJSON", toJsonStr,1, TimeUnit.DAYS);
            return data;
        }

    }
    public  Map<String, List<Catelog2Vo>> getCatalogJsonForDbWithRedisLock ()  {
        //TODO 本地锁synchronized，JUC(Lock)只能锁住当前进程，在分布式情况下想要锁住所有 只能使用分布式锁
        //设置过期时间，必须和加锁是同步的，原子的
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,30,TimeUnit.SECONDS);
        if (lock){
            log.warn("==>获取分布式锁成功<==");
            Map<String, List<Catelog2Vo>> data=null;
            try{
                //加锁成功执行业务
                data = getDataFromDb();
//            String s = redisTemplate.opsForValue().get("lock");
//            if (uuid.equals(s)){
//                //删除锁
//                //可能在获取锁的value后，自己的锁过期了，就会把别人的锁删除
//                 redisTemplate.delete("lock");//删除锁,只能删除自己的锁
//            }
            }finally {
                String script= "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁
                log.warn("==>删除锁<==");
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"),uuid);
            }
            return data;
        }else{
            log.warn("==>获取分布式锁失败.重新获取<==");
            //加锁失败
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return getCatalogJsonForDbWithRedisLock();//自旋
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        //得到锁后先到缓存中查一次，如果没有才继续查询
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //缓存不为空直接返回
            //log.warn("==>缓存命中,直接返回来catalogJSON<==");
            return JSONUtil.toBean(catalogJSON, Map.class);
        }
        log.warn("==>在数据库中查询了catalogJSON<==");
        //将数据库查询次数变成一次
        //查询所有分类信息
        List<CategoryEntity> allCategory = baseMapper.selectList(null);
        //获取所有一级分类
        List<CategoryEntity> category1Level =getParentCid(allCategory,0L);
        Map<String, List<Catelog2Vo>> map = category1Level.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询一级分类下对应的二级分类
            List<CategoryEntity> category2Level = getParentCid(allCategory,v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (category2Level != null && category2Level.size() != 0) {
                //封装成Vo
                catelog2Vos = category2Level.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setId(item.getCatId().toString());
                    catelog2Vo.setName(item.getName());
                    catelog2Vo.setCatalog1Id(item.getParentCid().toString());
                    //查询二级分类下的三级分类
                    List<CategoryEntity> category3Level = getParentCid(allCategory,item.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3Level != null && category3Level.size() != 0) {
                        //封装成VO
                        catelog3Vos = category3Level.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
                            catelog3Vo.setCatalog2Id(level3.getParentCid().toString());
                            catelog3Vo.setId(level3.getCatId().toString());
                            catelog3Vo.setName(level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        //将查询到的数据保存到redis
        String toJsonStr = JSONUtil.toJsonStr(map);
        redisTemplate.opsForValue().set("catalogJSON", toJsonStr,1, TimeUnit.DAYS);
        return map;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        // return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
        return collect;
    }

    public List<Long> findParentCid(List<Long> paths,Long catelogId){
        paths.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            return findParentCid(paths,category.getParentCid());
        }
        return paths;
    }


    /**
     * 从all中获取root的所有子分类（递归）
     * @param root 需要获取子分类的CategoryEntity
     * @param all 所有分类列表
     * @return
     */
    private List<CategoryEntity> getChildrenList(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children=all.stream().filter((categoryEntity)->{
            //返回所有分类列表中父id为root的id的分类
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map((categoryEntity)->{
            categoryEntity.setChildren(getChildrenList(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) ->{
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return  children;
    }
}