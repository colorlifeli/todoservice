package org.me.todoservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.me.todoservice.schema.Article;
import org.me.todoservice.utils.mybatis.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper {

	Article getById(@Param("id") int id);

	int add(@Param("a") Article a);

	int update(@Param("a") Article a);

	int delete(@Param("id") int id);

	List<Article> getByPage(@Param("page") Page page);

	/**
	 * 查询指定目录下的文章
	 * 
	 * @param page
	 * @return
	 */
	List<Article> getArticlesByPage(@Param("page") Page page, @Param("folderId") String folderId);


	List<Article> searchByPage(@Param("page") Page page, @Param("keyword") String keyword, @Param("status") String status);

	/**
	 * 查找逻辑删除了的文章
	 * @param page
	 * @return
	 */
	List<Article> getDeleteByPage(@Param("page") Page page);

	/**
	 * 将文章恢复为未删除
	 * @param id
	 * @return
	 */
	int recover(@Param("id") int id);
}
