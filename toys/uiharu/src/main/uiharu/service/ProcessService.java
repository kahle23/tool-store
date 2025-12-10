package uiharu.service;

import artoria.beans.BeanUtils;
import artoria.common.Result;
import artoria.exception.ExceptionUtils;
import artoria.exchange.JsonUtils;
import artoria.io.IOUtils;
import artoria.servlet.RequestUtils;
import artoria.util.StringUtils;
import uiharu.domain.RequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static artoria.common.Constants.*;

/**
 * Request process service.
 * @author Kahle
 */
@Service
public class ProcessService {
    private static Logger log = LoggerFactory.getLogger(ProcessService.class);

    public void html(HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestBean requestBean = this.requestBean(request);
            String result = requestBean != null ? requestBean.toString() : EMPTY_STRING;
            result = StringUtils.replace(result, NEWLINE, "<br />" + NEWLINE);
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            response.getWriter().write(result);
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public void json(HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestBean requestBean = this.requestBean(request);
            Result<RequestBean> result = new Result<RequestBean>(requestBean);
            response.setHeader("Content-Type", "application/json; charset=UTF-8");
            response.getWriter().write(JsonUtils.toJsonString(result));
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    private RequestBean requestBean(HttpServletRequest request) {
        try {
            if (request == null) { return null; }
            artoria.servlet.RequestBean requestBean = RequestUtils.getRequestBean(request);
            RequestBean result = BeanUtils.beanToBean(requestBean, RequestBean.class);
            ServletInputStream inputStream = request.getInputStream();
            result.setBody(IOUtils.toString(inputStream, UTF_8));
            log.info("{}{}", NEWLINE, result);
            return result;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
