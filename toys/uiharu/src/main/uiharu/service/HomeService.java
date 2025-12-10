package uiharu.service;

import artoria.data.Device;
import artoria.exception.ExceptionUtils;
import artoria.identifier.IdentifierUtils;
import artoria.servlet.RequestUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import misaka.device.DeviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;
import uiharu.pojo.dto.DeviceRecordDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Home service.
 * @author Kahle
 */
@Slf4j
@Service
public class HomeService {

    @Autowired
    private DeviceRecordService deviceRecordService;

    /*public void home(HttpServletRequest request, HttpServletResponse response) {
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
    }*/

    public void home(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(info(request));
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    private static final Parser uaParser;

    static {
        try {
            uaParser = new Parser();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public String info(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();

        String userAgent = RequestUtils.getUserAgent(request);
        builder.append("userAgent: ").append(userAgent).append("\n\n");

        Client client = uaParser.parse(userAgent);
        builder.append("userAgent info: ").append(JSON.toJSONString(client.userAgent, true)).append("\n");
        builder.append("os info: ").append(JSON.toJSONString(client.os, true)).append("\n");
        builder.append("device info: ").append(JSON.toJSONString(client.device, true)).append("\n");

        String deviceModel = client.device.family;

        Device device = DeviceUtils.findByDeviceModel(deviceModel);
        builder.append("device info: ").append(JSON.toJSONString(device, true)).append("\n");

        // 处理设备信息
        if (device != null) {
            try {
                DeviceRecordDTO deviceRecordDTO = new DeviceRecordDTO();
                deviceRecordDTO.setId(IdentifierUtils.nextStringIdentifier());
                deviceRecordDTO.setModel(deviceModel);
                deviceRecordService.addOrEditByDeviceId(deviceRecordDTO);
            }
            catch (Exception e) {
                log.info(">> error", e);
            }
        }

        log.info(builder.toString());

        String replaceAll = builder.toString().replaceAll("\n", "</br>");

        log.info(replaceAll);

        return replaceAll;
    }

}
