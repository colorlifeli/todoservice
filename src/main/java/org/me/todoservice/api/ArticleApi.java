package org.me.todoservice.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.me.todoservice.dao.ArticleMapper;
import org.me.todoservice.dao.CommonMapper;
import org.me.todoservice.schema.Article;
import org.me.todoservice.service.ToolService;
import org.me.todoservice.utils.ApiResponse;
import org.me.todoservice.utils.ToolUtil;
import org.me.todoservice.utils.mybatis.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/article")
public class ArticleApi {
	private static final Logger log = LoggerFactory.getLogger(ArticleApi.class);

	@Autowired
	private ArticleMapper articleMapper;
	@Autowired
	private ToolService toolService;
	@Autowired
	private CommonMapper commonMapper;

	private final static String cacheKey = "article:temp";

	@GetMapping(value = "/tempGet")
	public ApiResponse<Article> tempGet() {
		Article a = (Article) toolService.getCache(cacheKey);
		if (a == null) {
			a = new Article();
			a.setContent("");
		}
		return new ApiResponse<Article>(a);
	}

	@PostMapping(value = "/tempSave")
	public ApiResponse<Article> tempSave(@RequestBody Article a) {

		if (a.getFolders() != null) {
			String path = "";
			for (String folder : a.getFolders()) {
				path = path + "/" + folder;
			}
			a.setPath(path);
		}
		toolService.setCache(cacheKey, a);
		return ApiResponse.ok();
	}

	@GetMapping(value = "/tempDelete")
	public ApiResponse<Article> tempDelete() {
		toolService.setCache(cacheKey, null);
		return ApiResponse.ok();
	}

	@GetMapping(value = "/get/{id}")
	public ApiResponse<Article> get(@PathVariable(value = "id") int id) {
		Article a = articleMapper.getById(id);
		if (ToolUtil.isNotEmpty(a.getPath())) {
			String[] paths = a.getPath().split("/");
			List<String> folders = new ArrayList<String>();
			for (String path : paths) {
				if (ToolUtil.isNotEmpty(path)) {
					folders.add(path);
				}
			}
			a.setFolders(folders);
		}
		return new ApiResponse<Article>(a);
	}

	@PostMapping(value = "/add")
	public ApiResponse<Article> add(@RequestBody Article a) {
		Integer id = commonMapper.genId();
		a.setId(id.toString());
		String path = "";
		a.getFolders().remove(null);
		List<String> folders = a.getFolders();
		folders = folders.stream().filter(item -> item != null).collect(Collectors.toList());
		for (String folder : folders) {
			path = path + "/" + folder;
		}
		a.setFolders(folders);
		a.setPath(path);
		articleMapper.add(a);
		return new ApiResponse<Article>(a);
	}

	@PostMapping(value = "/update")
	public ApiResponse<Article> update(@RequestBody Article a) {
		articleMapper.update(a);
		return ApiResponse.ok();
	}

	@PostMapping(value = "/save")
	public ApiResponse<Article> save(@RequestBody Article a) {
		if (a.getFolders() != null) {
			String path = "";
			for (String folder : a.getFolders()) {
				if (folder == null)
					continue;
				path = path + "/" + folder;
				a.setFolderId(a.getFolders().get(a.getFolders().size() - 1));
			}
			a.setPath(path);
		}
		if (ToolUtil.isEmpty(a.getId())) {
			Integer id = commonMapper.genId();
			a.setId(id.toString());
			articleMapper.add(a);
		} else
			articleMapper.update(a);

		return new ApiResponse<Article>(a);
	}

	@GetMapping(value = "/delete/{id}")
	public ApiResponse<Article> delete(@PathVariable(value = "id") int id) {
		articleMapper.delete(id);
		return ApiResponse.ok();
	}

	@GetMapping(value = "/many/{pageNum}")
	public ApiResponse<Page<Article>> many(@PathVariable(value = "pageNum") Integer pageNum,
			@RequestParam(required = false) Integer pageSize) {
		if (pageNum == null) {
			pageNum = 1;
		}
		if (pageSize == null) 
			pageSize = 10;
		Page<Article> page = new Page<>(pageNum, pageSize);
		List<Article> articles = articleMapper.getByPage(page);
		page.getResult().addAll(articles);
		return new ApiResponse<Page<Article>>(page);
	}

	@PostMapping(value = "/edit")
	public ApiResponse<Article> edit(@RequestBody Article article) {
		Article a = articleMapper.getById(Integer.parseInt(article.getId()));
		if (ToolUtil.isNotEmpty(a.getPath())) {
			String[] paths = a.getPath().split("/");
			List<String> folders = new ArrayList<String>();
			for (String path : paths) {
				if (ToolUtil.isNotEmpty(path)) {
					folders.add(path);
				}
			}
			a.setFolders(folders);
		}
		return new ApiResponse<Article>(a);
	}

	@GetMapping(value = "/search")
	public ApiResponse<List<Article>> search(@RequestParam String keywords,@RequestParam(required = false) String status) {

		if(ToolUtil.isEmpty(status))
			status = "0";
		Page<Article> page = new Page<>(1, 20);
		page.setNotCount(true);
		List<Article> articles = articleMapper.searchByPage(page, keywords, status);
		return new ApiResponse<List<Article>>(articles);
	}


	@GetMapping(value = "/rubbish")
	public ApiResponse<List<Article>> rubbish() {
		Page<Article> page = new Page<>(1, Integer.MAX_VALUE);
		page.setNotCount(true);
		List<Article> articles = articleMapper.getDeleteByPage(page);
		return new ApiResponse<List<Article>>(articles);
	}


	@GetMapping(value = "/recover/{id}")
	public ApiResponse<Article> recover(@PathVariable(value = "id") int id) {
		articleMapper.recover(id);
		return ApiResponse.ok();
	}
}
