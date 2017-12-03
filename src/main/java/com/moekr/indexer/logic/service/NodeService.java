package com.moekr.indexer.logic.service;

import com.moekr.indexer.data.dao.NodeDAO;
import com.moekr.indexer.data.entity.Node;
import com.moekr.indexer.logic.vo.NodeVO;
import com.moekr.indexer.util.ToolKit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeService{
    private final NodeDAO nodeDAO;

    @Autowired
    public NodeService(NodeDAO nodeDAO){
        this.nodeDAO = nodeDAO;
    }

    @Transactional
    public void saveNode(Node node){
        nodeDAO.save(node);
    }

    public List<NodeVO> getNode(String path){
        String formattedPath = path.replaceAll("/+", "/");
        if(!formattedPath.equals("/")){
            String pathUri = formattedPath.substring(0, formattedPath.length() - 1);
            ToolKit.assertNotNull(nodeDAO.findById(DigestUtils.md5Hex(pathUri)));
        }
        return ToolKit.sort(nodeDAO.findByPath(formattedPath).stream().map(NodeVO::new).collect(Collectors.toList()), (node1, node2) -> {
            if(node1.isDirectory() != node2.isDirectory()){
                return BooleanUtils.compare(node2.isDirectory(), node1.isDirectory());
            }else{
                return StringUtils.compareIgnoreCase(node1.getName(),node2.getName());
            }
        });
    }

    @Transactional
    public void deleteNode(String nodeId){
        Node node = nodeDAO.findById(nodeId);
        if(node != null){
            nodeDAO.delete(node);
        }
    }
}
