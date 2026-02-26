package com.busanit501.springboot0226.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class SampleRestController {
    @GetMapping("/helloRest")
    public String[] helloRest(){
        log.info("REST API 확인");
        return new String[]{"ABC","DEF","GHI"};
    }
}
