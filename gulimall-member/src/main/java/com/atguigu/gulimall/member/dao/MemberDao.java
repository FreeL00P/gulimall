package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 16:56:57
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
