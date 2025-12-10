package uiharu.controller;

import uiharu.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request process controller.
 * @author Kahle
 */
//@Controller
public class ProcessController {
    private static Logger log = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ProcessService processService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/**", produces = {"text/html"})
    public void anyoneReturnHtml(HttpServletRequest request, HttpServletResponse response) {

        processService.html(request, response);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/**")
    public void anyoneReturnJson(HttpServletRequest request, HttpServletResponse response) {

        processService.json(request, response);
    }

}
