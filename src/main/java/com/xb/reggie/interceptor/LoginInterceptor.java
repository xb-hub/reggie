package com.xb.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.xb.reggie.common.BaseContext;
import com.xb.reggie.common.R;
import com.xb.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 执行拦截器逻辑，例如身份验证、权限检查等
        // 如果需要继续处理请求，返回true；如果需要中止请求处理，返回false
        String url = request.getRequestURI();
        log.info("URL: " + url);
        Object emp = request.getSession().getAttribute("employee");
        if (emp != null) {
            log.info("拦截失败");
            BaseContext.setCurrentId((Long)emp);
            return true;
        }
        log.info("拦截成功，跳转到登录界面");
        response.sendRedirect("/backend/page/login/login.html");
        return false;
    }
}
