package uiharu.controller;

import artoria.beans.BeanUtils;
import artoria.common.Input;
import artoria.common.PageResult;
import artoria.common.Result;
import artoria.util.PagingUtils;
import uiharu.persistence.entity.DeviceRecord;
import uiharu.pojo.dto.DeviceRecordDTO;
import uiharu.pojo.vo.DeviceRecordVO;
import uiharu.service.DeviceRecordService;
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
 * DeviceRecordController.
 * @author Kahle
 * @date 2020-05-24T22:43:47.965+0800
 */
@Slf4j
@Controller
public class DeviceRecordController {

    @Autowired
    private DeviceRecordService deviceRecordService;

    /*@ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Result<Object> hello(@RequestBody DeviceRecordVO deviceRecordVO) {

        return new Result<Object>("Hello, World! ");
    }*/

}
