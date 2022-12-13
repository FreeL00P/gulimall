package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * ListValueConstraintVaildator
 *
 * @author fj
 * @date 2022/12/12 22:21
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set=new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals= constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }

    //判断是否校验成功 value 需要校验的值
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(value);
    }
}
