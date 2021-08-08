package com.chat.app.models.Dtos;

import java.util.List;

public class PageDto<T> {
    private int pages;
    private List<T> data;

    public PageDto(int pages, List<T> data){
        this.pages = pages;
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
