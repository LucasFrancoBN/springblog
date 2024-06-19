package com.fiap.springblog.model;

import lombok.Data;

@Data
public class ArtigoStatusCount {
    private Integer status;
    private Integer count;

    public ArtigoStatusCount(Integer status, Integer count) {
        this.status = status;
        this.count = count;
    }

    public ArtigoStatusCount() {
    }
}
