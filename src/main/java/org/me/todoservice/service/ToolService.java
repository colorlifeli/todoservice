package org.me.todoservice.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ToolService {
	private static final Logger log = LoggerFactory.getLogger(ToolService.class);
	private static Map<String, Object> kvMap = new HashMap<String, Object>();

	public void setCache(String key, Object value) {
		kvMap.put(key, value);
	}

	public Object getCache(String key) {
		return kvMap.get(key);
	}
}
