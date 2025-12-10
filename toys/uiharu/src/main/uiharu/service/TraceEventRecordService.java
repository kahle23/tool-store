package uiharu.service;

import uiharu.pojo.dto.TraceEventRecordDTO;

/**
 * TraceEventRecordService.
 * @author Kahle
 * @date 2020-05-24T22:44:22.212+0800
 */
public interface TraceEventRecordService {

    /**
     * 插入记录
     */
    void insertSelective(TraceEventRecordDTO traceEventRecordDTO);

}
