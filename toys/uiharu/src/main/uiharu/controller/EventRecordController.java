package uiharu.controller;

import artoria.beans.BeanUtils;
import artoria.common.Input;
import artoria.common.PageResult;
import artoria.common.Result;
import artoria.util.PagingUtils;
import uiharu.persistence.entity.EventRecord;
import uiharu.pojo.dto.EventRecordDTO;
import uiharu.pojo.vo.EventRecordVO;
import uiharu.service.EventRecordService;
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
 * EventRecordController.
 * @author Kahle
 * @date 2020-05-24T22:44:07.754+0800
 */
@Slf4j
@Controller
public class EventRecordController {

    @Autowired
    private EventRecordService eventRecordService;

    /*@ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Result<Object> hello(@RequestBody EventRecordVO eventRecordVO) {

        return new Result<Object>("Hello, World! ");
    }*/

}
