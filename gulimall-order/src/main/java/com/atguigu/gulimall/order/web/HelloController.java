package com.atguigu.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * HelloController
 *
 * @author fj
 * @date 2023/2/15 20:38
 */
@Controller
public class HelloController {

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }
}
