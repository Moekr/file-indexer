package com.moekr.indexer.web.controller;

import com.moekr.indexer.logic.LogicConfig;
import com.moekr.indexer.logic.service.IconService;
import com.moekr.indexer.logic.service.NodeService;
import com.moekr.indexer.logic.vo.IconVO;
import com.moekr.indexer.logic.vo.NodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Controller
public class ViewController {
    private final LogicConfig logicConfig;
    private final NodeService nodeService;
    private final IconService iconService;

    @Autowired
    public ViewController(LogicConfig logicConfig, NodeService nodeService, IconService iconService) {
        this.logicConfig = logicConfig;
        this.nodeService = nodeService;
        this.iconService = iconService;
    }

    @GetMapping
    public String index(HttpServletRequest request, HttpServletResponse response, Map<String, Object> parameterMap) throws IOException {
        String uri = URLDecoder.decode(request.getRequestURI(),"UTF-8");
        List<NodeVO> nodeList = nodeService.getNode(uri);
        if(logicConfig.isRedirectOriginIndex() && nodeList.stream().anyMatch(node -> node.getName().equals("index.html"))){
            response.sendRedirect(uri + "index.html");
        }
        List<IconVO> iconList = iconService.loadNodeType(nodeList);
        parameterMap.put("uri", uri);
        parameterMap.put("nodeList", nodeList);
        parameterMap.put("iconList", iconList);
        return "index";
    }
}
