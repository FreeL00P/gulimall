package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:45
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增不能指定品牌id",groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须提交",groups = {UpdateGroup.class, AddGroup.class})//校验注解=>必须包含一个非空字符串
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(groups={AddGroup.class})
	@URL(message="logo必须是一个合法的url地址",groups = {UpdateGroup.class, AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */

	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vals={0,1},groups={AddGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups={AddGroup.class})
	@Pattern(regexp ="^[a-zA-Z]$",message="检索首字母必须是一个字母",groups = {UpdateGroup.class, AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups={AddGroup.class})
	@Min(value=0,message="排序必须大于等于0",groups = {UpdateGroup.class, AddGroup.class})
	private Integer sort;

}
