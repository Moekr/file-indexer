package com.moekr.indexer.logic.service;

import com.moekr.indexer.data.entity.Node;
import com.moekr.indexer.logic.LogicConfig;
import com.moekr.indexer.logic.vo.NodeVO;
import com.moekr.indexer.util.ToolKit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.List;

@Component
public class WatcherService extends FileAlterationListenerAdaptor {
	private final Log logger;

	private final LogicConfig logicConfig;
	private final NodeService nodeService;

	private String rootPath;
	private FileAlterationMonitor monitor;

	@Autowired
	public WatcherService(LogicConfig logicConfig, NodeService nodeService){
		this.logicConfig = logicConfig;
		this.nodeService = nodeService;
		logger = LogFactory.getLog(this.getClass());
	}

	@PostConstruct
	private void initialService() throws Exception {
		String directory = logicConfig.getDirectory();
		File root = new File(directory);
		if(!root.exists() || root.isFile()){
			throw new FileNotFoundException(root.getPath());
		}
		rootPath = root.getPath();
		logger.info("Setup monitor directory: " + rootPath);
		if(logicConfig.isSyncBeforeStart()){
			logger.info("Synchronize with database");
			synchronize(root);
		}
		FileAlterationObserver observer = new FileAlterationObserver(root);
		observer.addListener(this);
		monitor = new FileAlterationMonitor(logicConfig.getInterval(), observer);
		logger.info("Start file monitor");
		monitor.start();
	}

	private void synchronize(File directory) {
		File[] children = directory.listFiles();
		if(children != null){
			String path = directory.getPath().substring(rootPath.length());
			path = ToolKit.convertPath(path) + "/";
			List<NodeVO> nodeList = nodeService.getNode(path);
			for(File child : children){
				String fullPath = child.getPath().substring(rootPath.length());
				String nodeId = DigestUtils.md5Hex(ToolKit.convertPath(fullPath));
				if(!nodeList.removeIf(node -> node.getId().equals(nodeId))){
					onFileChange(child);
				}
				if(child.isDirectory()){
					synchronize(child);
				}
			}
			for(NodeVO node : nodeList){
				nodeService.deleteNode(node.getId());
			}
		}
	}

	@Override
	public void onDirectoryCreate(File directory) {
		onDirectoryChange(directory);
	}

	@Override
	public void onDirectoryChange(File directory) {
		onFileChange(directory);
	}

	@Override
	public void onDirectoryDelete(File directory) {
		onFileDelete(directory);
	}

	@Override
	public void onFileCreate(File file) {
		onFileChange(file);
	}

	@Override
	public void onFileChange(File file) {
		String fullPath = file.getPath().substring(rootPath.length());
		String path = fullPath.substring(0, fullPath.lastIndexOf(File.separatorChar) + 1);
		String name = file.getName();
		Node node = new Node();
		node.setId(DigestUtils.md5Hex(ToolKit.convertPath(fullPath)));
		node.setDirectory(file.isDirectory());
		node.setPath(ToolKit.convertPath(path));
		node.setName(name);
		node.setSize(file.isDirectory() ? 0L : file.length());
		node.setModifiedAt(ToolKit.convertTimeStamp(file.lastModified()));
		nodeService.saveNode(node);
	}

	@Override
	public void onFileDelete(File file) {
		String fullPath = file.getPath().substring(rootPath.length());
		nodeService.deleteNode(DigestUtils.md5Hex(ToolKit.convertPath(fullPath)));
	}

	@PreDestroy
	private void destroyService() throws Exception {
		logger.info("Stop file monitor");
		monitor.stop();
	}
}
