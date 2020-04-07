package org.me.todoservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Folder;
import org.me.todoservice.utils.mybatis.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderMapper {

	List<Folder> getByPage(@Param("page") Page page, @Param("folder") Folder folder);

	/**
	 * 查出此目录到根目录的路径
	 * 
	 * @param id
	 * @return
	 */
	List<Folder> getNav(@Param("id") String id);

	int add(@Param("f") Folder folder);

	int update(@Param("f") Folder folder);

	int delete(@Param("id") String id);

	/**
	 * 更新为非叶节点
	 * 
	 * @param id
	 * @return
	 */
	int updateNotLeaf(@Param("id") String id);
}
