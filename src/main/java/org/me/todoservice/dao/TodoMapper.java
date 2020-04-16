package org.me.todoservice.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Todo;
import org.me.todoservice.utils.mybatis.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoMapper {

    Todo getById(@Param("id") int id);

    List<Todo> listTodo(@Param("type") String type);

    List<Todo> listTodoAll(@Param("type") String type);

    int add(@Param("todo") Todo todo);

    int update(@Param("todo") Todo todo);

    int delete(@Param("id") int id);

    int toDone(@Param("id") int id);

    int backTodo(@Param("id") int id);

    List<Todo> listDoneByPage(@Param("page") Page page);

    List<Todo> searchByPage(@Param("page") Page page, @Param("start") Date start,
                            @Param("end") Date end, @Param("keyword") String keyword, @Param("type") String type);

}
