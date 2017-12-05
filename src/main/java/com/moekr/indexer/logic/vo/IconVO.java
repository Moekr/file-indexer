package com.moekr.indexer.logic.vo;

import lombok.Data;

@Data
public class IconVO {
    private String type;
    private String mime;
    private String base64;

    public IconVO(String type){
        this.type = type;
        this.base64 = "";
    }
}
