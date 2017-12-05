package com.moekr.indexer.web.controller;

import com.moekr.indexer.logic.service.IconService;
import com.moekr.indexer.logic.service.NodeService;
import com.moekr.indexer.logic.vo.IconVO;
import com.moekr.indexer.logic.vo.NodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Controller
public class ViewController {
    private final NodeService nodeService;
    private final IconService iconService;

    @Autowired
    public ViewController(NodeService nodeService, IconService iconService) {
        this.nodeService = nodeService;
        this.iconService = iconService;
    }

    @GetMapping
    public String index(HttpServletRequest request, Map<String, Object> parameterMap) throws UnsupportedEncodingException {
        String uri = URLDecoder.decode(request.getRequestURI(),"UTF-8");
        List<NodeVO> nodeList = nodeService.getNode(uri);
        List<IconVO> iconList = iconService.loadNodeType(nodeList);
        parameterMap.put("uri", uri);
        parameterMap.put("nodeList", nodeList);
        parameterMap.put("iconList", iconList);
        return "index";
    }
}
