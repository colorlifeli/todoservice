package org.me.todoservice.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.me.todoservice.dao.UserMapper;
import org.me.todoservice.schema.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String path = request.getRequestURI();
        if ("/user/login".equals(path) || path.startsWith("/static/") || path.startsWith("/error"))
            return true;
        String token = request.getHeader("Authorization");
        if (token == null)
            throw new MyException(Codes.AUTHFAIL, "无token，请重新登录");
        if (token != null) {
            JWTVerifier verifier = TokenUtil.getVerifier();
            try {
                DecodedJWT jwt = verifier.verify(token);
                String userCode = jwt.getClaim("usercode").asString();
                request.setAttribute("usercode", userCode);
                //没有检查 usercode的必要，不太可能不存在
//                User user = userMapper.get(userCode);
//                if (user == null || user.getCode() == null)
//                    throw new MyException(Codes.AUTHFAIL, "无此用户" + userCode + "，请重新登录");
            } catch (TokenExpiredException tee) {
                throw new MyException(Codes.AUTHFAIL, "登录已过期，请重新登录");
            } catch (JWTVerificationException jve) {
                log.error("veriry fail.", jve);
                throw new MyException(Codes.AUTHFAIL, "token验证失败，请重新登录");
            }
            return true;
        }

        return false;
    }
}