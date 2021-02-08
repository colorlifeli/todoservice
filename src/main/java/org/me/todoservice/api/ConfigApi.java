package org.me.todoservice.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.me.todoservice.dao.ConfigMapper;
import org.me.todoservice.schema.Article;
import org.me.todoservice.schema.Config;
import org.me.todoservice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/config")
public class ConfigApi {

    @Autowired
    private ConfigMapper configMapper;

    @GetMapping(value = "/get")
    public ApiResponse<Config> get() {
        List<Map<String, Object>> configs = configMapper.getAll();
        Map<String, String> map = new HashMap<>();
        for (Map<String, Object> config : configs) {
            String key = null, value = null;
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                if ("code".equals(entry.getKey()))
                    key = String.valueOf(entry.getValue());
                if ("value".equals(entry.getKey()))
                    value = String.valueOf(entry.getValue());
                map.put(key, value);
            }
        }

        Config c = new Config();
        c.setPageSize(Integer.parseInt(map.get("pagesize")));

        return new ApiResponse<Config>(c);
    }

    @PostMapping(value = "/save")
    public ApiResponse add(@RequestBody Config c) {

        configMapper.save("pagesize", String.valueOf(c.getPageSize()));

        return ApiResponse.ok();
    }
}
