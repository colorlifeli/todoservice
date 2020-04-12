package org.me.todoservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Todo;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoMapper {

	Todo getById(@Param("id") int id);

	List<Todo> listTodo();

	int add(@Param("todo") Todo todo);
}
