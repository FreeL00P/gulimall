package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String wareId=(String)  params.get("wareId");//仓库id
        if (!StringUtils.isEmpty(wareId)&&!"0".equalsIgnoreCase(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        String skuId=(String)  params.get("skuId");//商品id
        if (!StringUtils.isEmpty(skuId)&&!"0".equalsIgnoreCase(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //封装条件，判断需要添加的商品库存是否存在
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("ware_id",wareId)
                        .eq("sku_id",skuId);
        List<WareSkuEntity> entities = wareSkuDao.selectList(wrapper);
        if (entities.size()==0||entities==null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程调用查询sku的名字
            try{
                R info = productFeignService.info(skuId);
                if (info.getCode()==0) {
                    Map<String,Object> data =(Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            wareSkuDao.insert(wareSkuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }


    }


}