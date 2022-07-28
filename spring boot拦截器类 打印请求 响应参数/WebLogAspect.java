package com.dfdk.common.aspect;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author: guandezhi
 * @Date: 2019/3/5 21:17
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private final ThreadLocal<String> methodDescribe = new ThreadLocal<>();


    @Pointcut("execution(public * com.dfdk.controller..*.*(..))")
    public void webLog() {
    }


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        recordMethodLog(joinPoint, getRequestContext());
    }


    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        log.info(methodDescribe.get() + "返回参数 : {}",  JSONObject.toJSONString(ret));
        methodDescribe.remove();
    }


    private HttpServletRequest getRequestContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    private void recordMethodLog(JoinPoint joinPoint, HttpServletRequest request) {
        Method method = getMethod(joinPoint);
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        String value = "";
        if (null != apiOperation) {
            value = apiOperation.value();
            if (StringUtils.isNotEmpty(value)) {
                methodDescribe.set(value);
            }
        }

        log.info(value + "入参：{} ", parseArgs(joinPoint));
    }


    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getMethod();
    }

    private String parseArgs(JoinPoint joinPoint) {
        StringBuffer sb = new StringBuffer();
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof BindingResult ||
                        arg instanceof HttpServletRequest ||
                        arg instanceof HttpServletResponse ||
                        arg instanceof MultipartFile ||
                        arg instanceof MultipartFile[]) {
                    continue;
                }
                sb.append(" ");
                sb.append(JSONObject.toJSONString(arg));
            }
        }
        return sb.toString();
    }
}
