package com.taotao.shop.web.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>Title: PageController</p>
 * <p>Description: 展示登录和注册页面的Controller</p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-06 18:17</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @RequestMapping("/register")
    public String showRegister() {
        return "register";
    }

    @RequestMapping("/login")
    public String showLogin() {
        return "login";
    }

}