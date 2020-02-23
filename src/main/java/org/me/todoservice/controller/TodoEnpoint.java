package org.me.todoservice.controller;

import org.me.todoservice.dao.TodoMapper;
import org.me.todoservice.schema.Todo;
import org.me.todoservice.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人待办
 * 
 * @author opq
 *
 */
@RestController
@RequestMapping(path = "/todo/self")
public class TodoEnpoint {
	private static final Logger log = LoggerFactory.getLogger(TodoEnpoint.class);

	@Autowired
	private TodoMapper todoMapper;

	@GetMapping(value = "/todo/{id}")
	public ApiResponse<Todo> getTodo(@PathVariable(value = "id", required = true) int id) {
		Todo todo = todoMapper.getById(id);
		return new ApiResponse<Todo>(todo);
	}

	@PostMapping(value = "/add")
	public ApiResponse<Todo> add(@RequestBody Todo todo) {
		int i = todoMapper.add(todo);
		if (i == 1)
			return ApiResponse.ok();

		return ApiResponse.fail();
	}
}
