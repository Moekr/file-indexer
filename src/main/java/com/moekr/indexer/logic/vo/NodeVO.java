package com.moekr.indexer.logic.vo;

import com.moekr.indexer.data.entity.Node;
import com.moekr.indexer.util.ToolKit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class NodeVO {
    private String id;
    private boolean directory;
    private String path;
    private String name;
    private String size;
    private String date;

    public NodeVO(Node node){
        BeanUtils.copyProperties(node, this);
        size = directory ? "-" : ToolKit.convertSize(node.getSize());
        date = ToolKit.convertDate(node.getModifiedAt());
    }
}
