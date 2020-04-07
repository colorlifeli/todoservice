package org.me.todoservice.schema.vo;

import java.util.List;

import org.me.todoservice.schema.Article;
import org.me.todoservice.schema.Folder;

public class SubFilesVo {

	private List<Article> articles;
	private List<Folder> folders;
	private List<Folder> nav;
	private Integer total;

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public List<Folder> getNav() {
		return nav;
	}

	public void setNav(List<Folder> nav) {
		this.nav = nav;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

}
