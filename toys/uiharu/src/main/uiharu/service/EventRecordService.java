package uiharu.service;

import uiharu.pojo.dto.EventRecordDTO;

/**
 * EventRecordService.
 * @author Kahle
 * @date 2020-05-24T22:44:07.754+0800
 */
public interface EventRecordService {

    void insertSelective(EventRecordDTO eventRecordDTO);

}
