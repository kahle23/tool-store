package uiharu.service.impl;

import artoria.beans.BeanUtils;
import artoria.exception.BusinessException;
import artoria.exception.VerifyUtils;
import artoria.identifier.IdentifierUtils;
import artoria.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uiharu.persistence.entity.EventRecord;
import uiharu.persistence.mapper.EventRecordMapper;
import uiharu.pojo.dto.EventRecordDTO;
import uiharu.service.EventRecordService;

import java.util.Date;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.SYSTEM;
import static uiharu.common.UiharuErrorCode.UIHARU001;

/**
 * EventRecordServiceImpl.
 * @author Kahle
 * @date 2020-05-24T22:44:07.754+0800
 */
@Slf4j
@Service
public class EventRecordServiceImpl implements EventRecordService {

    @Autowired
    private EventRecordMapper eventRecordMapper;

    @Override
    public void insertSelective(EventRecordDTO eventRecordDTO) {
        VerifyUtils.notNull(eventRecordDTO, UIHARU001);
        EventRecord eventRecord =
                BeanUtils.beanToBean(eventRecordDTO, EventRecord.class);
        if (StringUtils.isBlank(eventRecord.getId())) {
            eventRecord.setId(IdentifierUtils.nextStringIdentifier());
        }
        eventRecord.setCreateUser(SYSTEM);
        eventRecord.setCreateDate(new Date());
        eventRecord.setUpdateUser(SYSTEM);
        eventRecord.setUpdateDate(new Date());
        eventRecord.setAliveFlag(ONE);
        int effect = eventRecordMapper.insertSelective(eventRecord);
        if (effect != ONE) {
            throw new BusinessException("新增事件记录失败！");
        }
    }

}
