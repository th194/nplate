package com.netive.nplate.paging;

import com.netive.nplate.domain.PageDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PagingResponse<T> {

    private List<T> list = new ArrayList<>();
    private PageDTO page;

    public PagingResponse(List<T> list, PageDTO page) {
        this.list = list;
        this.page = page;
    }

    public PagingResponse(PageDTO page) {
        this.page = page;
    }
}
