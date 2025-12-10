package uiharu.controller;

import artoria.beans.BeanUtils;
import artoria.common.Input;
import artoria.common.PageResult;
import artoria.common.Result;
import artoria.util.PagingUtils;
import uiharu.persistence.entity.TraceEventRecord;
import uiharu.pojo.dto.TraceEventRecordDTO;
import uiharu.pojo.vo.TraceEventRecordVO;
import uiharu.service.TraceEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * TraceEventRecordController.
 * @author Kahle
 * @date 2020-05-24T22:44:22.212+0800
 */
@Slf4j
@Controller
public class TraceEventRecordController {

    @Autowired
    private TraceEventRecordService traceEventRecordService;

    /*@ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Result<Object> hello(@RequestBody TraceEventRecordVO traceEventRecordVO) {

        return new Result<Object>("Hello, World! ");
    }*/

}
