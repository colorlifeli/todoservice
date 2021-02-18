package org.me.todoservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "printIpFilter", urlPatterns = "/*")
public class PrintIpFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PrintIpFilter.class);
    private static final String COMMA = ",";

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("PrintIpFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String remoteAddr = getIpAddr(httpRequest);
            //String operation = httpRequest.getRequestURI();
            String operation = httpRequest.getRequestURL().toString();
            log.info("remoteAddr :" + remoteAddr);
            log.info("path: " + operation);
        } catch (Exception e) {
            log.error("error!", e);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("PrintIpFilter destroy");
    }

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     *
     *  0:0:0:0:0:0:0:1 和 127.0.0.1 表示本地地址
     * @param request {@link HttpServletRequest}
     * @return ipaddr
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = null;
        String unkownStr = "unknown";
        if (request == null) {
            return "";
        }

        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || unkownStr.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || unkownStr.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || unkownStr.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || unkownStr.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || unkownStr.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用代理，则获取第一个IP地址
        int ipLength = 15;
        if (!StringUtils.isEmpty(ip) && ip.length() > ipLength) {
            if (ip.indexOf(COMMA) > 0) {
                ip = ip.substring(0, ip.indexOf(COMMA));
            }
        }

        return ip;
    }
}
