package kz.bitlab.robygroup.sppmid.core.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
class CustomErrorController implements ErrorController {
    public static final String DEFAULT_ERROR_VIEW = "404";
    private final static String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
    @RequestMapping(value = ERROR_PATH)
    public String errorHtml() {
        return "404";
    }

}