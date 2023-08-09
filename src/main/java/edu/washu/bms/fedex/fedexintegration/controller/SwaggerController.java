package edu.washu.bms.fedex.fedexintegration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {

    @RequestMapping({"/"})
    public String getSwaggerApiDocsPage() {
        return "redirect:swagger-ui.html";
    }
}
