package org.me.todoservice.dao;

import org.me.todoservice.schema.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    User get(String code);
}
