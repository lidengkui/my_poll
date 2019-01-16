package com.poll.entity.ext;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {
    private List<T> records=new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private int offset;
    private int totalCount;


    public Page() {
        this(0,0);
    }

    public Page(int current, int size) {
        this.currentPage = current<=0?1:current;
        this.pageSize = size<=0?10:size;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getCurrent() {
        return currentPage;
    }

    public void setCurrent(int current) {
        this.currentPage = current<=0?1:current;
    }

    public int getSize() {
        return pageSize;
    }

    public void setSize(int size) {
        this.pageSize = size<=0?10:size;
    }

    public int getOffset() {
        return (currentPage-1)*pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
