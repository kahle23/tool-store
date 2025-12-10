package uiharu.service.impl;

import artoria.beans.BeanUtils;
import artoria.exception.BusinessException;
import artoria.exception.VerifyUtils;
import artoria.identifier.IdentifierUtils;
import artoria.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uiharu.persistence.entity.TraceEventRecord;
import uiharu.persistence.mapper.TraceEventRecordMapper;
import uiharu.pojo.dto.TraceEventRecordDTO;
import uiharu.service.TraceEventRecordService;

import java.util.Date;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.SYSTEM;
import static uiharu.common.UiharuErrorCode.UIHARU001;

/**
 * TraceEventRecordServiceImpl.
 * @author Kahle
 * @date 2020-05-24T22:44:22.212+0800
 */
@Slf4j
@Service
public class TraceEventRecordServiceImpl implements TraceEventRecordService {

    @Autowired
    private TraceEventRecordMapper traceEventRecordMapper;

    @Override
    public void insertSelective(TraceEventRecordDTO traceEventRecordDTO) {
        VerifyUtils.notNull(traceEventRecordDTO, UIHARU001);
        TraceEventRecord traceEventRecord =
                BeanUtils.beanToBean(traceEventRecordDTO, TraceEventRecord.class);
        if (StringUtils.isBlank(traceEventRecord.getId())) {
            traceEventRecord.setId(IdentifierUtils.nextStringIdentifier());
        }
        traceEventRecord.setCreateUser(SYSTEM);
        traceEventRecord.setCreateDate(new Date());
        traceEventRecord.setUpdateUser(SYSTEM);
        traceEventRecord.setUpdateDate(new Date());
        traceEventRecord.setAliveFlag(ONE);
        int effect = traceEventRecordMapper.insertSelective(traceEventRecord);
        if (effect != ONE) {
            throw new BusinessException("新增追踪事件记录失败！");
        }
    }

}
