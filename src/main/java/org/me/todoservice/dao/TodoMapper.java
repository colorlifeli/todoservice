package org.me.todoservice.dao;

import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Todo;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoMapper {

	Todo getById(@Param("id") int id);

	int add(Todo todo);
}
