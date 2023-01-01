package com.atguigu.gulimall.member.service.impl;

import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneException;
import com.atguigu.gulimall.member.exception.UsernameException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();
        //设置默认等级
        //查询默认等级
        MemberLevelEntity levelEntity=baseMapper.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());
        //检查用户手机是否唯一
        checkPhoneUnique(vo.getPhone());
        entity.setMobile(vo.getPhone());
        checkUsernameUnique(vo.getUsername());
        entity.setUsername(vo.getUsername());
        //密码加密处理
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        baseMapper.insert(entity);
    }

    @Override
    public void checkUsernameUnique(String username) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count>0){
            throw new UsernameException();
        }

    }

    @Override
    public void checkPhoneUnique(String phone) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count>0){
            throw new PhoneException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        //去数据库查询
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct)
                .or()
                .eq("mobile", loginacct));
        if (entity==null){
            //登陆失败
            return null;
        }
        //密码匹配
        String pwdDb = entity.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(password, pwdDb);
        if (matches){
          return entity;
        }else{
            //登陆失败
            return null;
        }

    }
}