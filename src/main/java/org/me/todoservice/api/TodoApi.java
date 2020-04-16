package org.me.todoservice.api;

import java.util.Date;
import java.util.List;

import org.me.todoservice.dao.CommonMapper;
import org.me.todoservice.dao.TodoMapper;
import org.me.todoservice.schema.Todo;
import org.me.todoservice.utils.ApiResponse;
import org.me.todoservice.utils.mybatis.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

/**
 * 个人待办
 *
 * @author opq
 */
@RestController
@RequestMapping(path = "/todo")
public class TodoApi {
    private static final Logger log = LoggerFactory.getLogger(TodoApi.class);

    @Autowired
    private TodoMapper todoMapper;
    @Autowired
    private CommonMapper commonMapper;

    @GetMapping(value = "/listTodo")
    public ApiResponse<List<Todo>> listTodo(@RequestParam(required = false) String type) {
        List<Todo> todos = todoMapper.listTodo(type);
        return new ApiResponse<List<Todo>>(todos);
    }

    @GetMapping(value = "/listTodoAll")
    public ApiResponse<List<Todo>> listTodoAll(@RequestParam(required = false) String type) {
        List<Todo> todos = todoMapper.listTodoAll(type);
        return new ApiResponse<List<Todo>>(todos);
    }

    @GetMapping(value = "delete/{id}")
    public ApiResponse<Todo> delete(@PathVariable(value = "id", required = true) int id) {
        todoMapper.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping(value = "/add")
    public ApiResponse<Todo> add(@RequestBody Todo todo) {
        Integer id = commonMapper.genId();
        todo.setId(id);
        int i = todoMapper.add(todo);
        if (i == 1)
            return new ApiResponse<Todo>(todo);

        return ApiResponse.fail();
    }

    @PostMapping(value = "/update")
    public ApiResponse<Todo> update(@RequestBody Todo todo) {
        int i = todoMapper.update(todo);
        if (i == 1)
            return ApiResponse.ok();

        return ApiResponse.fail();
    }

    @GetMapping(value = "toDone/{id}")
    public ApiResponse<Todo> toDone(@PathVariable(value = "id", required = true) int id) {
        todoMapper.toDone(id);
        return ApiResponse.ok();
    }

    @GetMapping(value = "backTodo/{id}")
    public ApiResponse<Todo> backTodo(@PathVariable(value = "id", required = true) int id) {
        todoMapper.backTodo(id);
        return ApiResponse.ok();
    }

    @GetMapping(value = "/listDone")
    public ApiResponse<Page> listDone(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        if (pageNum == null)
            pageNum = 1;
        if (pageSize == null)
            pageSize = 10;
        Page page = new Page(pageNum, pageSize);
        List<Todo> todos = todoMapper.listDoneByPage(page);
        page.getResult().addAll(todos);
        return new ApiResponse<Page>(page);
    }

    @GetMapping(value = "/search")
    public ApiResponse<Page> search(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss") Date start,
											@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss") Date end,
											@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String type,
											@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        if (pageNum == null)
            pageNum = 1;
        if (pageSize == null)
            pageSize = 10;
        Page page = new Page(pageNum, pageSize);
        List<Todo> todos = todoMapper.searchByPage(page, start, end, keyword, type);
        page.getResult().addAll(todos);
        return new ApiResponse<Page>(page);
    }
}
