package cn.com.nwdc.db.pageinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.*;

/**
 * @author tianyao
 * @Classname PageInfo
 * @Description TODO
 * @Date 2019/9/25 15:43
 * @group smart video north
 */

@Data
@JsonIgnoreProperties(value = {"offset", "defaultSortFiled", "defaultSortDirect", "SortInfo","orderMap"})
public class PageInfo<DATA> {
    /**
     * 排序字段与排序方式映射
     */
    protected Map<String, String> orderMap;
    /**
     * 总记录数
     */
    protected long totalCount;

    /**
     * 每页记录数默认10
     */
    protected int pageSize = 10;

    /**
     * 数据库查询数据起点
     */
    protected int offset;

    public int getOffset() {
        if (pageSize >= 0 && pageIndex >= 0) {
            offset = (pageIndex - 1) * pageSize;
        }
        return offset;
    }

    /**
     * 总页数
     */
    protected long pageCount;

    /**
     * 当前页
     */
    protected int pageIndex = 1;
    /**
     * 返回具体结果
     */
    protected List<DATA> dataList;

    private String groupKey;

    private String groupVal;

    private Map<String,PageInfo> groups = new LinkedHashMap<>();
    /**
     * 默认用第一列数据排序
     */
    protected String defaultSortFiled = "1";
    /**
     * 默认排序方式
     */
    protected String defaultSortDirect = "DESC";

    @Data
    class SortInfo {
        /**
         * 排序字段
         */
        private String sortField;
        /**
         * 排序方式
         */
        private String sortDirect;

        public SortInfo(String sortField, String sortDirect) {
            this.sortField = sortField;
            this.sortDirect = sortDirect;
        }
    }

    /**
     * 空参构造
     */
    public PageInfo() {

    }

    public void putGroup(String groupVal,DATA data,PageInfo groupPage){
        if(!groups.containsKey(groupVal)){

            PageInfo newGroupPageInfo = new PageInfo();
            if(null != groupPage){
                newGroupPageInfo.setPageSize(groupPage.getPageSize());
                newGroupPageInfo.setPageIndex(groupPage.getPageIndex());
            }
            groups.put(groupVal, newGroupPageInfo);
        }
        groups.get(groupVal).addData(data);
    }
    public void setPageSize(int pageSize) {
        if (pageSize == 0 || "".equals(pageSize + "")) {
            this.pageSize = 10;
        }else {
            this.pageSize = pageSize;
        }
    }

    public void setPageIndex(int pageIndex) {
        if (pageIndex == 0 || "".equals(pageIndex + "")) {
            this.pageIndex = 1;
        }else {
            this.pageIndex = pageIndex;
        }
    }

    /**
     * 根据每页数量创建对象,没有值，采用默认10
     *
     * @param pageSize 分页记录数
     */
    public PageInfo(int pageSize) {
        setPageSize(pageSize);
        this.pageSize = pageSize;
        if (pageSize == 0) {
            this.pageSize = 10;
        }
        //设置总页数
        if (this.totalCount > 0) {
            this.pageCount = this.totalCount / this.pageSize;
            if ((this.totalCount % this.pageSize) > 0) {
                this.pageCount += 1;
            }
        }
    }

    /**
     * 根据分页记录数和当前页码创建对象
     *
     * @param pageSize  分页记录数
     * @param pageIndex 页码
     */
    public PageInfo(int pageSize, int pageIndex) {
        setPageSize(pageSize);
        setPageIndex(pageIndex);
    }

    /**
     * 根据分页记录数、当前页码和总记录数创建对象
     *
     * @param pageSize   分页记录数
     * @param pageIndex  页码
     * @param totalCount 总记录数
     */
    public PageInfo(int pageSize, int pageIndex, long totalCount) {
        setPageSize(pageSize);
        setPageIndex(pageIndex);
        setTotalCount(totalCount);
    }


    /**
     * 获取排序字段和排序方式的映射,转换成List来供给mybatis使用
     *
     * @return
     */
    public List<SortInfo> getOrderMap() {
        List<SortInfo> sortInfos = new ArrayList<>();
        if (this.orderMap != null && this.orderMap.size() > 0) {
            Set<Map.Entry<String, String>> entries = this.orderMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (entry.getValue() == null || "".equals(entry.getValue())) {
                    //根据是否传入排序字段的排序方式选择，不传默认DESC
                    sortInfos.add(new SortInfo(entry.getKey(), "DESC"));
                } else {
                    if (!(entry.getValue().toLowerCase().equals("asc") || entry.getValue().toLowerCase().equals("desc"))) {
                        throw new IllegalArgumentException("排序方向必须为 ASC 或者DESC 。");
                    }
                    sortInfos.add(new SortInfo(entry.getKey(), entry.getValue()));
                }
            }
        } else {
        }
        return sortInfos;
    }

    public void setOrderMap(Map<String, String> orderMap) {
        this.orderMap = orderMap;
    }

    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总页数
     *
     * @param totalCount
     */
    public void setTotalCount(long totalCount) {
        if (totalCount == 0) {
            this.totalCount = 0;
            this.pageCount = 0;
        } else {
            this.totalCount = totalCount;
            if (pageSize == 0) {
                this.pageSize = 10;
            }
            this.pageCount = this.totalCount / this.pageSize;
            if ((this.totalCount % this.pageSize) > 0) {
                this.pageCount += 1;
            }
        }
    }


    public void addData(DATA t){
        if(this.dataList == null){
            this.dataList = Lists.newLinkedList();
        }
        this.dataList.add(t);
    }
    public void setDataList(List<DATA> dataList) {
        this.dataList = dataList;
        if (this.totalCount == -1) {
            int curPageRowCount = 0;
            if (this.dataList != null) {
                curPageRowCount = this.dataList.size();
            }
            //最后一页，如果没有计算总数的情况下自动计算总数。
            if (this.pageSize > curPageRowCount) {
                setTotalCount((this.pageIndex - 1) * this.pageSize + curPageRowCount);
            }
        }
    }


    public PageInfo<DATA> getGroupPage(String key){

        return groups.get(key);
    }


}
