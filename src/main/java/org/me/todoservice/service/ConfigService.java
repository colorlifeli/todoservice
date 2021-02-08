package org.me.todoservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.me.todoservice.dao.ConfigMapper;
import org.me.todoservice.schema.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;
    
	public Config getConfig() {
        List<Map<String, Object>> configs = configMapper.getAll();
        Map<String, String> map = new HashMap<>();
        for (Map<String, Object> config : configs) {
            String key = null, value = null;
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                if ("CODE".equals(entry.getKey()))
                    key = String.valueOf(entry.getValue());
                if ("VALUE".equals(entry.getKey()))
                    value = String.valueOf(entry.getValue());
                map.put(key, value);
            }
        }

        Config c = new Config();
        c.setPageSize(Integer.parseInt(map.get("pagesize")));
        
		return c;
	}
}
