package uiharu.common;

import artoria.spring.ApplicationContextUtils;
import artoria.util.StringUtils;
import uiharu.service.HomeService;
import uiharu.service.ProcessService;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request processing servlet.
 * @author Kahle
 */
@WebServlet(urlPatterns="/*", name="request-processing-servlet")
public class RequestProcessingServlet extends HttpServlet {
    private static final String TEXT_HTML = "text/html";
    private static final String URL_INDEX = "/index";
    private static final String URL_ROOT = "/";
    private static final String ACCEPT = "Accept";
    private ProcessService processService;
    private HomeService homeService;

    @Override
    public void init() throws ServletException {
        processService = ApplicationContextUtils.getBean(ProcessService.class);
        homeService = ApplicationContextUtils.getBean(HomeService.class);
    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        HttpServletResponse response = resp instanceof HttpServletResponse ? (HttpServletResponse) resp : null;
        HttpServletRequest request = req instanceof HttpServletRequest ? (HttpServletRequest) req : null;
        if (request == null || response == null) { return; }
        String requestURI = request.getRequestURI();
        if (URL_ROOT.equals(requestURI)
                || URL_INDEX.equalsIgnoreCase(requestURI)) {
            homeService.home(request, response);
            return;
        }
        String accept = request.getHeader(ACCEPT);
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        if (accept != null && accept.contains(TEXT_HTML)) {
            processService.html(request, response);
        }
        else {
            processService.json(request, response);
        }
    }

}
