package org.me.todoservice.api;

        import org.me.todoservice.dao.UserMapper;
        import org.me.todoservice.schema.User;
        import org.me.todoservice.utils.ApiResponse;
        import org.me.todoservice.utils.MyException;
        import org.me.todoservice.utils.TokenUtil;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.HashMap;
        import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserApi {
    private static final Logger log = LoggerFactory.getLogger(UserApi.class);
    @Autowired
    UserMapper userMapper;

    @PostMapping(value = "/login")
    public ApiResponse<Map<String, String>> login(@RequestBody User user) {
        if (user.getCode() != null) {
            User u = userMapper.get(user.getCode());
            if (u == null || u.getCode() == null)
                throw new MyException("登录失败，用户名或密码错误");
            if (user.getPassword() != null && !user.getPassword().equals(u.getPassword()))
                throw new MyException("登录失败，用户名或密码错误");

            log.info("{} login success", user.getCode());
            Map<String, String> result = new HashMap<>();
            result.put("token", TokenUtil.sign(u));
            return new ApiResponse<>(result);
        }
        return ApiResponse.fail();
    }
}
