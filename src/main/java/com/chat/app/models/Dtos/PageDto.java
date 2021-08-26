package com.chat.app.models.Dtos;

import java.util.List;

public class PageDto<T> {
    private long count;
    private List<T> data;

    public PageDto(long pages, List<T> data){
        this.count = pages;
        this.data = data;
    }

    public PageDto(){
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
