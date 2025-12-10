package uiharu.controller;

import org.springframework.stereotype.Controller;
import uiharu.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Home controller.
 * @author Kahle
 */
@Controller
public class HomeController {
    private static Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private HomeService homeService;

    @RequestMapping("/")
    public String home(HttpServletRequest request, HttpServletResponse response) {

        return "redirect:/index";
    }

    @RequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response) {

        homeService.home(request, response);
    }

}
