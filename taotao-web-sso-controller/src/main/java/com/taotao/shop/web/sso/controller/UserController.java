package com.taotao.shop.web.sso.controller;

import com.taotao.shop.web.sso.common.utils.CookieUtils;
import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.result.TaotaoResult;
import com.taotao.springboot.sso.export.UserResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private UserResource userResource;

    @Value("${TOKEN_KEY}")
    private String TOKEN_KEY;


    @RequestMapping("/check/{param}/{type}")
    @ResponseBody
    public TaotaoResult checkData(
            @PathVariable String param, @PathVariable Integer type) {
        return userResource.checkData(param, type);
    }


    @RequestMapping(value="/register", method=RequestMethod.POST)
    @ResponseBody
    public TaotaoResult register(TbUser user) {
        return userResource.register(user);
    }


    @RequestMapping(value="/login", method= RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(String username, String password,
                              HttpServletResponse response, HttpServletRequest request) {
        TaotaoResult result = userResource.login(username, password);
        // 登录成功后，写入cookie
        if (result.getStatus() == 200) {
            CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString());
        }
        return result;
    }


/*	@RequestMapping(value="/token/{token}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE	// 指定响应数据的content-type
			)
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback) {
		TaotaoResult result = userService.getUserByToken(token);
		// 判断是否为jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + JsonUtils.objectToJson(result) + ");";
		}
		return JsonUtils.objectToJson(result);
	}	*/

    // jsonp方法二：Spring4.1以上版本使用
    @RequestMapping(value="/token/{token}", method=RequestMethod.GET)
    @ResponseBody
    public Object getUserByToken(@PathVariable String token, String callback) {
        TaotaoResult result = userResource.getUserByToken(token);
        if (StringUtils.isNotBlank(callback)) {		// 判断是否为jsonp请求
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            // 设置回调方法
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return result;
    }


    @RequestMapping(value="/logout/{token}",
            method=RequestMethod.GET,
            produces= MediaType.APPLICATION_JSON_UTF8_VALUE	// 指定响应数据的content-type
    )
    @ResponseBody
    public TaotaoResult logout(@PathVariable String token, String callback) {
        TaotaoResult result = userResource.logout(token);
        return result;
    }


}
