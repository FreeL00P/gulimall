package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.vo.UserLoginVo;
import com.atguigu.gulimall.authserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * MemberFeignService
 *
 * @author fj
 * @date 2022/12/31 7:18
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
