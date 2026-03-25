package com.busanit501.springboot0226.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {

    @RequestMapping("/error/403")
    public String ex403() {
        // templates/error/403.html 파일을 반환
        return "error/403";
    }
}
