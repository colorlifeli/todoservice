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
import org.me.todoservice.service.ConfigService;
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

    @Autowired
    private ConfigService configService;

    @GetMapping(value = "/get")
    public ApiResponse<Config> get() {

        return new ApiResponse<Config>(configService.getConfig());
    }

    @PostMapping(value = "/save")
    public ApiResponse add(@RequestBody Config c) {

        configMapper.save("pagesize", String.valueOf(c.getPageSize()));

        return ApiResponse.ok();
    }
}
