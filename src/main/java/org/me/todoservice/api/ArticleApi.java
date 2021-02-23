package org.me.todoservice.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.me.todoservice.dao.ArticleMapper;
import org.me.todoservice.dao.CommonMapper;
import org.me.todoservice.dao.FolderMapper;
import org.me.todoservice.schema.Article;
import org.me.todoservice.schema.Folder;
import org.me.todoservice.service.ConfigService;
import org.me.todoservice.service.ToolService;
import org.me.todoservice.utils.AbstractApi;
import org.me.todoservice.utils.ApiResponse;
import org.me.todoservice.utils.MyException;
import org.me.todoservice.utils.ToolUtil;
import org.me.todoservice.utils.mybatis.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/article")
public class ArticleApi extends AbstractApi {
	private static final Logger log = LoggerFactory.getLogger(ArticleApi.class);

	@Autowired
	private ArticleMapper articleMapper;
	@Autowired
	private ToolService toolService;
	@Autowired
	private CommonMapper commonMapper;
    @Autowired
    private ConfigService configService;
	@Autowired
	private FolderMapper folderMapper;

	private final static String IMG_PATH = "static/upload/";

	private final static String cacheKey = "article:temp";

	@GetMapping(value = "/tempGet")
	public ApiResponse<Article> tempGet() {
		Article a = (Article) toolService.getCache(cacheKey);
		if (a == null) {
			a = new Article();
			a.setContent("");
		}
		return new ApiResponse<>(a);
	}

	@PostMapping(value = "/tempSave")
	public ApiResponse<Article> tempSave(@RequestBody Article a) {
		if (a.getFolders() != null) {
			StringBuilder path = new StringBuilder();
			for (String folder : a.getFolders()) {
				path.append("/").append(folder);
			}
			a.setPath(path.toString());
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
			List<String> folders = new ArrayList<>();
			for (String path : paths) {
				if (ToolUtil.isNotEmpty(path)) {
					folders.add(path);
				}
			}
			a.setFolders(folders);
		}
		return new ApiResponse<>(a);
	}

	@PostMapping(value = "/add")
	public ApiResponse<Article> add(@RequestBody Article a) {
		int id = commonMapper.genId();
		a.setId(String.valueOf(id));
		StringBuilder path = new StringBuilder();
		a.getFolders().remove(null);
		List<String> folders = a.getFolders();
		folders = folders.stream().filter(item -> item != null).collect(Collectors.toList());
		for (String folder : folders) {
			path.append("/").append(folder);
		}
		a.setFolders(folders);
		a.setPath(path.toString());
		a.setUsercode(getUserCode());
		articleMapper.add(a);
		return new ApiResponse<>(a);
	}

	@PostMapping(value = "/update")
	public ApiResponse<Article> update(@RequestBody Article a) {
		articleMapper.update(a);
		return ApiResponse.ok();
	}

	@PostMapping(value = "/save")
	public ApiResponse<Article> save(@RequestBody Article a) {
		if (a.getFolders() != null && a.getFolders().size() > 0) {
			StringBuilder path = new StringBuilder();
			//既记录 path，也记录所在的目录id，方便查询目录下所有的文章
			String lastFolderId = a.getFolders().get(a.getFolders().size() - 1);
			a.setFolderId(lastFolderId);
			for (String folder : a.getFolders()) {
				if (folder == null)
					continue;
				path.append("/").append(folder);
			}
			a.setPath(path.toString());
		}
		if (ToolUtil.isEmpty(a.getId())) {
			int id = commonMapper.genId();
			a.setId(String.valueOf(id));
		} else
			articleMapper.update(a);

		return new ApiResponse<>(a);
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
			pageSize = configService.getConfig().getPageSize();
		Page<Article> page = new Page<>(pageNum, pageSize);
		List<Article> articles = articleMapper.getByPage(page, getUserCode());
		page.getResult().addAll(articles);
		return new ApiResponse<>(page);
	}

	@PostMapping(value = "/edit")
	public ApiResponse<Article> edit(@RequestBody Article article) {
		Article a = articleMapper.getById(Integer.parseInt(article.getId()));
		if (ToolUtil.isNotEmpty(a.getPath())) {
			String[] paths = a.getPath().split("/");
			List<String> folders = new ArrayList<>();
			for (String path : paths) {
				if (ToolUtil.isNotEmpty(path)) {
					folders.add(path);
				}
			}
			a.setFolders(folders);
		}
		return new ApiResponse<>(a);
	}

	@GetMapping(value = "/search")
	public ApiResponse<List<Article>> search(@RequestParam String keywords,@RequestParam(required = false) String status) {

		if(ToolUtil.isEmpty(status))
			status = "0";
		Page<Article> page = new Page<>(1, 20);
		page.setNotCount(true);
		List<Article> articles = articleMapper.searchByPage(page, keywords, status, getUserCode());
		return new ApiResponse<>(articles);
	}


	@GetMapping(value = "/rubbish")
	public ApiResponse<List<Article>> rubbish() {
		Page<Article> page = new Page<>(1, Integer.MAX_VALUE);
		page.setNotCount(true);
		List<Article> articles = articleMapper.getDeleteByPage(page, getUserCode());
		return new ApiResponse<>(articles);
	}


	@GetMapping(value = "/recover/{id}")
	public ApiResponse<Article> recover(@PathVariable(value = "id") int id) {
		Article a = articleMapper.getById(id);
		Folder f = folderMapper.get(a.getFolderId());
		if(f == null || f.getId() == null)
			throw new MyException("所在目录已删除，不能恢复");
		articleMapper.recover(id);
		return ApiResponse.ok();
	}

	@PostMapping(value = "/uploadImg")
	public ApiResponse<String> uploadImg(MultipartFile file, @RequestParam String articleId) throws FileNotFoundException {
		SimpleDateFormat sdf = new SimpleDateFormat("_yyMMdd_HHmmss_");
		String time = sdf.format(new Date());
		ApplicationHome ah = new ApplicationHome(Article.class);
		String realPath = ah.getSource().getParentFile().toString() + "/";

		realPath += IMG_PATH;
		File folder = new File(realPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String oldName = file.getOriginalFilename();
		String newName = articleId + time + oldName;
		try {
			file.transferTo(new File(folder,newName));
		} catch (IOException e) {
			log.info(realPath);
			log.error("保存图片失败", e);
			return ApiResponse.fail("保存图片失败");
		}
		return new ApiResponse<>(IMG_PATH + newName);
	}

	@DeleteMapping(value = "/deleteImg")
	public ApiResponse<String> deleteImg(@RequestParam String fileName) {

		ApplicationHome ah = new ApplicationHome(Article.class);
		String realPath = ah.getSource().getParentFile().toString() + "/";
		log.info(realPath + fileName);
		File file = new File(realPath + fileName);
		if (file.exists()) {
			if(file.delete())
				return ApiResponse.ok();
		}

		return ApiResponse.fail("删除图片失败");
	}
}
