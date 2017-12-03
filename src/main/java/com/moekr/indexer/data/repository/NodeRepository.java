package com.moekr.indexer.data.repository;

import com.moekr.indexer.data.entity.Node;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NodeRepository extends CrudRepository<Node, String> {
	List<Node> findAllByPath(String path);
}
