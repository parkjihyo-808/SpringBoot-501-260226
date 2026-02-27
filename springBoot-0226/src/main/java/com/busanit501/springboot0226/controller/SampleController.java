package com.busanit501.springboot0226.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class SampleController {

    // 내부 클래스로 수업용으로 만듦.
    public class SampleDTO {
        private String p1,p2,p3;

        public String getP1() {
            return p1;
        }
        public String getP2() {
            return p2;
        }
        public String getP3() {
            return p3;
        }
    }

    @GetMapping("/ex/ex3")
    public void ex3(Model model) {
        log.info("/ex/ex3 확인.....");
        List<String> strList = Arrays.asList("111", "BBB", "CCC");
        model.addAttribute("strList", strList);
    }


    @GetMapping("/ex/ex2")
    public void ex2(Model model) {
        log.info("/ex/ex2 확인.....");
        List<String> strList = Arrays.asList("111", "BBB", "CCC");
        model.addAttribute("strList", strList);

        Map<String, String> map = new HashMap<>();
        map.put("1", "1111");
        map.put("2", "2222");
        model.addAttribute("map", map);

        SampleDTO sampleDTO = new SampleDTO();
        sampleDTO.p1 = "p1 sample value";
        sampleDTO.p2 = "p2 sample value";
        sampleDTO.p3 = "p3 sample value";
        model.addAttribute("sampleDTO", sampleDTO);
    }

    @GetMapping("/hello")
    public void hello(Model model) {
        log.info("hello 확인.....");
        model.addAttribute("msg", "헬로우 월드 Hello World");
    }

    // templates/ex/ex1.html 생성해야함.
    @GetMapping("/ex/ex1")
    public void ex1(Model model) {
        log.info("/ex/ex1 확인.....");
        List<String> strList = Arrays.asList("111", "BBB", "CCC");
        model.addAttribute("strList", strList);
    }

}
