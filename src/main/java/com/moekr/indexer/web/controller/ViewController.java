package com.moekr.indexer.web.controller;

import com.moekr.indexer.logic.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@Controller
public class ViewController {
    private final NodeService nodeService;

    @Autowired
    public ViewController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public String index(HttpServletRequest request, Map<String, Object> parameterMap) throws UnsupportedEncodingException {
        String uri = URLDecoder.decode(request.getRequestURI(),"UTF-8");
        parameterMap.put("uri", uri);
        parameterMap.put("list", nodeService.getNode(uri));
        return "index";
    }
}
