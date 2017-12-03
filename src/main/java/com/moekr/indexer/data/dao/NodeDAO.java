package com.moekr.indexer.data.dao;

import com.moekr.indexer.data.entity.Node;
import com.moekr.indexer.data.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NodeDAO extends AbstractDAO<Node, String> {
    private final NodeRepository repository;

    @Autowired
    public NodeDAO(NodeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Node> findByPath(String path) {
        return repository.findAllByPath(path);
    }
}
