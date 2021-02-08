package org.me.todoservice.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Config;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMapper {

    List<Map<String,Object>> getAll();

    int save(@Param("code") String code, @Param("value") String value);
}
