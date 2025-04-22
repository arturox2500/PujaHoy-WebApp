package com.webapp08.pujahoy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class spaController {
    @GetMapping({ "/new/**/{path:[^\\.]*}", "/{path:new[^\\.]*}" })
    public String redirect() {
        return "forward:/new/index.html";
    }

}
