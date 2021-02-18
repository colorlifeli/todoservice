package org.me.todoservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.me.todoservice.schema.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class TokenUtil {
    private static final Logger log = LoggerFactory.getLogger(TokenUtil.class);
    private static final long EXPIRE_TIME = 10*24*60*60*1000;
    private static final String TOKEN_SECRET = "todoservice";  //密钥盐

    /**
     * 签名生成
     *
     * @param user
     * @return
     */
    public static String sign(User user) {
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
//            return JWT.create()
//                    .withIssuer(ISSUER)         //发布者
//                    .withAudience(audience)     //观众，相当于接受者
//                    .withIssuedAt(new Date())   // 生成签名的时间
//                    .withExpiresAt(DateUtils.offset(new Date(),2, Calendar.HOUR))    // 生成签名的有效期
//                    .withClaim("data", JSON.toJSONString(data)) //自定义key/value对，存数据
//                    .withNotBefore(new Date())  //生效时间
//                    .withJWTId(UUID.randomUUID().toString())    //编号
//                    .sign(algorithm);                           //签入
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("usercode", user.getCode())
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
            log.info(token);
        } catch (Exception e) {
            log.error("sing exception", e);
        }
        return token;
    }

    /**
     * 签名验证
     *
     * @return
     */
    public static JWTVerifier getVerifier() {
        return JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();


//        System.out.println("username: " + jwt.getClaim("usercode").asString());
//        System.out.println("过期时间：      " + jwt.getExpiresAt());
    }
}
