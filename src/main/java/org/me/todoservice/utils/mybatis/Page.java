package org.me.todoservice.utils.mybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * service-comb 要求，必须是标准java bean（get/set方法与变量完全一致)，因此先注释没有变量对应的方法  --2018/11/12
 * @author lijiancan
 *
 * @param <T>
 */
//@ApiModel(value = "Page", description = "查询结果页")
public class Page<T>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE_SIZE = 20;

    //@ApiModelProperty(value = "页大小", required = true, example = "20")
    private int pageSize = 20;

    //@ApiModelProperty(value = "开始页", required = true, example = "0")
    private long start;

    //@ApiModelProperty(value = "查询结果列表", required = true)
    private List<T> result;

    //@ApiModelProperty(value = "总条数", required = true, example = "0")
    private long totalCount;

    private String message;

    //@ApiModelProperty(value = "是否计算总数，false:计算；true:不计算", example = "false")
    private boolean notCount = false; //是否计算总数，默认计算

    public Page() {
        this(0L, 0L, DEFAULT_PAGE_SIZE, new ArrayList());
    }

    public Page(Integer pageNum, Integer pageSize) {

        if (pageNum == null || pageNum < 1){
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.setPageSize(pageSize);
        this.setStart((pageNum - 1) * pageSize);
        this.result = new ArrayList<>();
    }

    public Page(long start, long totalSize, int pageSize, List data) {
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.result = data;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    //@ApiModelProperty(value = "总页数")
    public long getTotalPageCount() {
        if (this.totalCount % this.pageSize == 0L) {
            return this.totalCount / this.pageSize;
        }
        return this.totalCount / this.pageSize + 1L;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public List<T> getResult() {
        return this.result;
    }

//    //@ApiModelProperty(value = "当前数")
//    public long getCurrentPageNo() {
//        return this.start / this.pageSize + 1L;
//    }
//
//    public boolean hasNextPage() {
//        return getCurrentPageNo() < getTotalPageCount() - 1L;
//    }
//
//    public boolean hasPreviousPage() {
//        return getCurrentPageNo() > 1L;
//    }

//    protected static int getStartOfPage(int pageNo) {
//        return getStartOfPage(pageNo, 20);
//    }
//
//    public static int getStartOfPage(int pageNo, int pageSize) {
//        return (pageNo - 1) * pageSize;
//    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getStart() {
        return this.start;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setStart(long start) {
        this.start = start;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isNotCount() {
        return notCount;
    }

    public void setNotCount(boolean notCount) {
        this.notCount = notCount;
    }
}
