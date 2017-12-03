package com.moekr.indexer.logic.service;

import com.moekr.indexer.data.entity.Node;
import com.moekr.indexer.util.ToolKit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class WatcherService implements Runnable {
	private final Log logger;

	@Value("${indexer.directory}")
	private String directory;

	private NodeService nodeService;

	private WatchService watchService;
	private boolean closed;
	private Map<WatchKey, String> pathMap;

	@Autowired
	public WatcherService(NodeService nodeService){
		this.nodeService = nodeService;
		logger = LogFactory.getLog(this.getClass());
	}

	@PostConstruct
	private void initialService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
		pathMap = new HashMap<>();
		registerWatchService(new File(directory));
		new Thread(this, "watch-service").start();
	}

	private void registerWatchService(File node) {
		if(!node.exists() || !node.isDirectory()){
			return;
		}
		Path path = Paths.get(node.toURI());
		try {
			WatchKey watchKey = path.register(watchService,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE
			);
			String uriPath = ToolKit.convertPath(node.getPath().substring(directory.length()));
			if(!uriPath.endsWith("/")){
				uriPath = uriPath + "/";
			}
			pathMap.put(watchKey, uriPath);
		} catch (IOException e) {
			logger.error("Register watcher fail: " + node);
		}
		File[] children = node.listFiles();
		if(children != null){
			Arrays.stream(children).filter(File::isDirectory).forEach(this::registerWatchService);
		}
	}

	@Override
	public void run() {
		WatchKey watchKey;
		while(!closed){
			try {
				watchKey = watchService.take();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				continue;
			}
			for(WatchEvent<?> watchEvent : watchKey.pollEvents()){
				String path = pathMap.get(watchKey);
				String name = watchEvent.context().toString();
				String uri = path + name;
				File file = new File(directory + ToolKit.convertUri(uri));
				logger.info(watchEvent.kind().name() + ": " + uri);
				switch (watchEvent.kind().name()){
					case "ENTRY_DELETE":
						nodeService.deleteNode(DigestUtils.md5Hex(uri));
						break;
					case "ENTRY_CREATE":
						if(file.isDirectory()){
							registerWatchService(file);
						}
					case "ENTRY_MODIFY":
						Node node = new Node();
						node.setId(DigestUtils.md5Hex(uri));
						node.setDirectory(file.isDirectory());
						node.setPath(path);
						node.setName(name);
						node.setSize(file.isDirectory() ? 0 : file.length());
						node.setModifiedAt(ToolKit.convertTimeStamp(file.lastModified()));
						nodeService.saveNode(node);
						break;
					default:
						logger.error("Unknown event kind: " + watchEvent.kind().name());
				}
			}
			if(!watchKey.reset()){
				pathMap.remove(watchKey);
			}
		}
	}

	@PreDestroy
	private void destroyService() throws IOException {
		closed = true;
		watchService.close();
	}
}
