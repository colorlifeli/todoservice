package org.me.todoservice.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractApi {
    public String getUserCode() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String usercode = (String) request.getAttribute("usercode");
        if (StringUtils.isEmpty(usercode))
            throw new MyException("获取不到usercode");
        return usercode;
    }
}
