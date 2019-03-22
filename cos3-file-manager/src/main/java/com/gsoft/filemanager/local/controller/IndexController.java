package com.gsoft.filemanager.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {


    @RequestMapping("/fileIndex")
    public String index() {
        return "fileIndex";
    }
}
