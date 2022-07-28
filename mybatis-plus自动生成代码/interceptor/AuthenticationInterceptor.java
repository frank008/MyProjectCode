package com.dfdk.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTVerificationException;


import com.dfdk.common.utils.JwtTokenUtil;
import com.dfdk.common.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private final JwtTokenUtil jwtTokenUtil;


    //免登录访问路径
    @Value("${logincheck.paths}")
    private String paths;

    //开启登录校验
    @Value("${logincheck.right}")
    private String right;


    //全部拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (right.equals("false")) {
            return true;
        }
        //可以直接访问的地址
        String path = request.getServletPath();
        if (paths.contains(path)) {
            log.info("免登陆访问地址：" + path);
            return true;
        }
        boolean returnBack = false;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Result athResponse = new Result<>();
        athResponse.error();

        String errorStr;
        String userToken = request.getHeader("userToken");
        if (StringUtils.isBlank(userToken)) {
            userToken = request.getParameter("userToken");
        }

        //TODO 登录拦截未完成
        if (StringUtils.isNotBlank(userToken) && !StringUtils.equals(userToken, "null")) {
            // String memberId = jwtTokenUtil.parseToken(userToken).getSubject();


           /* YinseUser memberInfo = memberInfoService.getById(memberId);
            if (memberInfo != null) {
                try {
                    jwtTokenUtil.verify(userToken, memberInfo);
                    request.setAttribute("memberId", memberId);
                    return true;
                } catch (JWTVerificationException e) {
                    errorStr = "登录信息过期，授权失败。";
                    athResponse.setCode(401);
                    athResponse.setMsg(errorStr);
                }
            } else {
                errorStr = "用户信息异常，授权失败。";
                athResponse.setMsg(errorStr);
                log.info("用户信息异常，授权失败。");
            }*/

        } else {
            errorStr = "登录信息为空，授权失败。";
            athResponse.setMsg(errorStr);
            log.info(method.getName() + ": 登录信息为空，授权失败。");
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(JSON.toJSONString(athResponse));

        return returnBack;
    }
}
