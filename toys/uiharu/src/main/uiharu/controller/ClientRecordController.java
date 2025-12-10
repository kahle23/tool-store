package uiharu.controller;

import artoria.beans.BeanUtils;
import artoria.common.Input;
import artoria.common.PageResult;
import artoria.common.Result;
import artoria.util.PagingUtils;
import uiharu.persistence.entity.ClientRecord;
import uiharu.pojo.dto.ClientRecordDTO;
import uiharu.pojo.vo.ClientRecordVO;
import uiharu.service.ClientRecordService;
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
 * ClientRecordController.
 * @author Kahle
 * @date 2020-05-24T22:43:33.621+0800
 */
@Slf4j
@Controller
public class ClientRecordController {

    @Autowired
    private ClientRecordService clientRecordService;

    /*@ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Result<Object> hello(@RequestBody ClientRecordVO clientRecordVO) {

        return new Result<Object>("Hello, World! ");
    }*/

}
