package com.hmall.user.common;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class CheckLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Enumeration<String> names = request.getHeaderNames();
        Integer userid=null;
        while (names.hasMoreElements()){
            String name = names.nextElement();
            if (name.equals("authorization")){
                Integer header = Integer.valueOf(request.getHeader(name));
                userid=header;
            }
        }
        if (userid!=null){
            UserId.se(userid);
        }else {
            return false;
        }
        return true;
    }
}
