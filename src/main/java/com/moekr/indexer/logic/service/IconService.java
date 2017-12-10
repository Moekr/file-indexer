package com.moekr.indexer.logic.service;

import com.moekr.indexer.logic.LogicConfig;
import com.moekr.indexer.logic.vo.IconVO;
import com.moekr.indexer.logic.vo.NodeVO;
import org.apache.commons.codec.binary.Base64InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IconService {
    private static final String DIRECTORY_TYPE = "dir";
    private static final String DEFAULT_FILE_TYPE = "file";

    private final LogicConfig logicConfig;

    private final Map<String, IconVO> suffixMap;
    private final Map<String, IconVO> typeMap;

    @Autowired
    public IconService(LogicConfig logicConfig){
        this.logicConfig = logicConfig;
        suffixMap = new HashMap<>();
        typeMap = new HashMap<>();
        typeMap.put(DIRECTORY_TYPE, new IconVO(DIRECTORY_TYPE));
        typeMap.put(DEFAULT_FILE_TYPE, new IconVO(DEFAULT_FILE_TYPE));
    }

    @PostConstruct
    private void initialService() throws IOException {
        InputStream suffixDefineInputStream;
        if(logicConfig.getIcon().getSuffixDefine() == null){
            suffixDefineInputStream = this.getClass().getClassLoader().getResourceAsStream("icon/suffix-define.properties");
        }else{
            suffixDefineInputStream = new FileInputStream(logicConfig.getIcon().getSuffixDefine());
        }
        loadSuffixDefine(suffixDefineInputStream);
        suffixDefineInputStream.close();
        InputStream typeDefineInputStream;
        if(logicConfig.getIcon().getTypeDefine() == null){
            typeDefineInputStream = this.getClass().getClassLoader().getResourceAsStream("icon/type-define.properties");
        }else{
            typeDefineInputStream = new FileInputStream(logicConfig.getIcon().getTypeDefine());
        }
        loadTypeDefine(typeDefineInputStream);
        typeDefineInputStream.close();
    }

    private void loadSuffixDefine(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        for(String suffix : properties.stringPropertyNames()){
            String type = properties.getProperty(suffix);
            typeMap.putIfAbsent(type, new IconVO(type));
            IconVO icon = typeMap.get(type);
            suffixMap.put(suffix, icon);
        }
    }

    private void loadTypeDefine(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        for(String type : properties.stringPropertyNames()){
            IconVO icon = typeMap.get(type);
            if(icon != null){
                String iconFile = properties.getProperty(type);
                if(iconFile.lastIndexOf('.') > 0){
                    String suffix = iconFile.substring(iconFile.lastIndexOf('.') + 1);
                    switch (suffix){
                        case "jpg":
                            icon.setMime("image/jpeg");
                            break;
                        case "png":
                            icon.setMime("image/png");
                            break;
                        case "bmp":
                            icon.setMime("image/bmp");
                            break;
                        case "ico":
                            icon.setMime("image/x-icon");
                            break;
                        case "svg":
                            icon.setMime("image/svg+xml");
                            break;
                        default:
                            icon.setMime("image");
                    }
                }else {
                    icon.setMime("image");
                }
                icon.setBase64(base64Encode(properties.getProperty(type)));
            }
        }
    }

    private String base64Encode(String iconFile) throws IOException {
        InputStream inputStream;
        if(logicConfig.getIcon().getIconDirectory() == null){
            inputStream = this.getClass().getClassLoader().getResourceAsStream("icon/" + iconFile);
        }else{
            inputStream = new FileInputStream(logicConfig.getIcon().getIconDirectory() + iconFile);
        }
        Base64InputStream base64InputStream = new Base64InputStream(inputStream, true, 0, null);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(base64InputStream));
        String base64 = bufferedReader.readLine();
        bufferedReader.close();
        return base64;
    }

    public List<IconVO> loadNodeType(List<NodeVO> nodeList){
        Set<String> typeSet = new HashSet<>();
        typeSet.add(DIRECTORY_TYPE);
        typeSet.add(DEFAULT_FILE_TYPE);
        for(NodeVO node : nodeList){
            if(node.isDirectory()){
                node.setType(DIRECTORY_TYPE);
            }else if(node.getName().lastIndexOf('.') > 0){
                String suffix = node.getName().substring(node.getName().lastIndexOf('.') + 1).toLowerCase();
                IconVO icon = suffixMap.get(suffix);
                if(icon != null){
                    node.setType(icon.getType());
                    typeSet.add(icon.getType());
                }else{
                    node.setType(DEFAULT_FILE_TYPE);
                }
            }else{
                node.setType(DEFAULT_FILE_TYPE);
            }
        }
        return typeSet.stream().map(typeMap::get).collect(Collectors.toList());
    }
}
