package com.taotao.springboot.web.sso.controller;

import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.result.TaotaoResult;
import com.taotao.springboot.sso.export.UserResource;
import com.taotao.springboot.web.sso.common.utils.CookieUtils;
import com.taotao.springboot.web.sso.common.utils.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Title: UserController</p>
 * <p>Description: 用户管理Controller</p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-06 18:11</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserResource userResource;

    @Value("${TOKEN_KEY}")
    private String TOKEN_KEY;

    /**
     * 判断用户提交的数据是否合法、或可用
     */
    @RequestMapping("/check/{param}/{type}")
    @ResponseBody
    public TaotaoResult checkData(
            @PathVariable String param, @PathVariable Integer type) {
        return userResource.checkData(param, type);
    }

    /**
     * 用户注册
     */
    @RequestMapping(value="/register", method=RequestMethod.POST)
    @ResponseBody
    public TaotaoResult register(TbUser user) {
        log.info("用户注册, user={}", JacksonUtils.objectToJson(user));
        TaotaoResult result = userResource.register(user);
        log.info("用户注册, res={}", JacksonUtils.objectToJson(result));
        return result;
    }

    /**
     * 用户登录
     */
    @RequestMapping(value="/login", method= RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(String username, String password,
                              HttpServletResponse response, HttpServletRequest request) {
        // #1 登录
        log.info("用户登录, username={}", username);
        TaotaoResult result = userResource.login(username, password);
        log.info("用户登录, res={}", JacksonUtils.objectToJson(result));
        // #2 登录成功后，将TOKEN令牌写入Cookie
        if (result.getStatus() == 200) {
            CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString());
        }
        return result;
    }

    /**
     * 判断用户是否已登录
     * jsonp跨域请求
     */
    @RequestMapping(value="/token/{token}", method=RequestMethod.GET)
    @ResponseBody
    public Object getUserByToken(@PathVariable String token, String callback) {
        log.info("根据Token获取用户信息, token={}", token);
        TaotaoResult result = userResource.getUserByToken(token);
        log.info("根据Token获取用户信息, res={}", JacksonUtils.objectToJson(result));
        if (StringUtils.isNotBlank(callback)) {		// 判断是否为jsonp请求
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            // 设置回调方法
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return result;
    }

    /**
     * 用户退出
     */
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // #1 获取存储于Cookie中的TOKEN令牌
        String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
        // #2 删除Cookie
        CookieUtils.deleteCookie(request, response, TOKEN_KEY);
        // #3 退出
        log.info("用户退出, token={}", token);
        TaotaoResult result = userResource.logout(token);
        log.info("用户退出, res={}", JacksonUtils.objectToJson(result));
        response.sendRedirect("http://localhost:8086");
        //return "redirect:http://localhost:8086";
    }

}
