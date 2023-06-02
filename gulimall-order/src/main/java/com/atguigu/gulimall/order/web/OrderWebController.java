package com.atguigu.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * OrderWebController
 *
 * @author fj
 * @date 2023/2/25 17:19
 */
@Controller
public class OrderWebController {

    @GetMapping("/toTrade")
    public String toTrade(){
        return "confirm";
    }

}
