package http.client.debug.server.service;

import artoria.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static artoria.common.Constants.NEWLINE;

/**
 * Home service.
 * @author Kahle
 */
@Service
public class HomeService {

    public void home(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE HTML>").append(NEWLINE);
        builder.append("<html>").append(NEWLINE);
        builder.append("<head>").append(NEWLINE);
        builder.append("<title>").append("HTTP client debug server").append("</title>").append(NEWLINE);
        builder.append("</head>").append(NEWLINE);
        builder.append("<body>").append(NEWLINE);
        builder.append("<h3>").append(NEWLINE);
        builder.append("HTTP client debug server").append(NEWLINE);
        builder.append("</h3>").append(NEWLINE);
        builder.append("HTTP client debug server is mainly used to debug HTTP client tools " +
                "or some problems when sending ajax. ").append("<br />").append(NEWLINE);
        builder.append("You can call any address (except '/' and '/index') " +
                "and the server will return information about the request, " +
                "such as how to pass parameters, for debugging ajax requests " +
                "or HTTP client tool development or debugging.").append("<br />").append(NEWLINE);
        builder.append("</body>").append(NEWLINE);
        builder.append("</html>").append(NEWLINE);
        try {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(builder.toString());
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
