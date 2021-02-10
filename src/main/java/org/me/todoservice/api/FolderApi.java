package org.me.todoservice.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.me.todoservice.dao.ArticleMapper;
import org.me.todoservice.dao.FolderMapper;
import org.me.todoservice.schema.Article;
import org.me.todoservice.schema.Folder;
import org.me.todoservice.schema.vo.SubFilesVo;
import org.me.todoservice.service.ConfigService;
import org.me.todoservice.service.FolderSevice;
import org.me.todoservice.utils.ApiResponse;
import org.me.todoservice.utils.MyException;
import org.me.todoservice.utils.mybatis.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/folder")
public class FolderApi {

	@Autowired
	private FolderMapper folderMapper;
	@Autowired
	private ArticleMapper articleMapper;
    @Autowired
    private ConfigService configService;
	@Autowired
	private FolderSevice folderSevice;

	@GetMapping(value = "/subFolder")
	public ApiResponse<List> get(@RequestParam(required = false) String parentid) {
		Folder folder = new Folder();
		folder.setParentId(parentid);
		Page page = new Page<>(1, Integer.MAX_VALUE);
		List<Folder> folders = folderMapper.getByPage(page, folder);
		// 对结果进行转换
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (Folder f : folders) {
			Map<String, Object> item = new HashMap<String, Object>();
			// item.put("value", Integer.parseInt(f.getId()));
			item.put("value", f.getId());
			if ("1".equals(f.getLeaf()))
				item.put("leaf", true);
			else
				item.put("leaf", false);
			item.put("label", f.getTitle());
			item.put("children", null);
			results.add(item);
		}
		return new ApiResponse<>(results);
	}

	/**
	 * 返回根目录
	 * 
	 * @return
	 */
	@GetMapping(value = "/current")
	public ApiResponse<List<Folder>> current() {
		Folder f = new Folder();
		f.setTitle("Home");
		List<Folder> list = new ArrayList<Folder>();
		list.add(f);
		return new ApiResponse<>(list);
	}

	@GetMapping(value = "/subFiles/{pageNum}")
	public ApiResponse<SubFilesVo> subFiles(@PathVariable(value = "pageNum") Integer pageNum,
			@RequestParam(required = false) String folderId) {

		int pageSize = configService.getConfig().getPageSize();
		if (pageNum == null) {
			pageNum = 1;
		}
		Page page = new Page<>(pageNum, pageSize);
		// page.setNotCount(true);
		List<Article> articles = new ArrayList<Article>();

		// 先查文件夹，如果文件夹超过 pagesize，则只取pagesize条，且不再查articles了。
		Folder folder = new Folder();
		folder.setParentId(folderId);
		List<Folder> folders = folderMapper.getByPage(page, folder);
		Long folderNum = page.getTotalCount();

		// folder数量少于pagesize，继续查article
		if (folders.size() < pageSize) {
			page.setStart(page.getStart() - folderNum);
			if (folders.size() != 0)
				page.setPageSize(pageSize - folders.size());
			articles = articleMapper.getArticlesByPage(page, folderId);
		} else {
			page.setPageSize(1);
			// 为了获取 total
			articleMapper.getArticlesByPage(page, folderId);
		}
		Long articleNum = page.getTotalCount();

		// 导航列表
		List<Folder> nav = folderMapper.getNav(folderId);
		Folder home = new Folder();
		home.setTitle("Home");
		// 增加Home目录
		nav.add(home);

		SubFilesVo result = new SubFilesVo();
		result.setArticles(articles);
		result.setFolders(folders);
		result.setNav(nav);
		result.setTotal((int) (folderNum + articleNum));
		result.setPageSize(pageSize);
		return new ApiResponse<>(result);
	}

	@PostMapping(value = "/add")
	public ApiResponse<Folder> add(@RequestBody Folder a) {
		folderMapper.add(a);
		if ("1".equals(a.getLeaf()))
			folderMapper.updateNotLeaf(a.getParentId());
		return ApiResponse.ok();
	}

	@PostMapping(value = "/update")
	public ApiResponse<Folder> update(@RequestBody Folder a) {
		folderMapper.update(a);
		return ApiResponse.ok();
	}

	@GetMapping(value = "/delete")
	public ApiResponse<Folder> delete(@RequestParam String folderId)  {
		Page page = new Page<>(1, Integer.MAX_VALUE);
		List<Article> articles = articleMapper.getAllArticlesByPage(page, folderId);
		if (articles != null && articles.size() > 0) {
			throw new MyException("该目录下有文章，不能删除！");
		}
		folderSevice.delete(folderId);
		return ApiResponse.ok();
	}

}
