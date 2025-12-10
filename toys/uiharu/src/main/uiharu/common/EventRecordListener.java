package uiharu.common;

import artoria.beans.BeanUtils;
import artoria.data.Event;
import artoria.event.EventObject;
import artoria.event.EventType;
import artoria.message.Message;
import artoria.message.TargetedMessageListener;
import artoria.util.MapUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uiharu.pojo.dto.EventRecordDTO;
import uiharu.pojo.dto.TraceEventRecordDTO;
import uiharu.service.EventRecordService;
import uiharu.service.TraceEventRecordService;

import java.util.Map;

import static artoria.common.Constants.EMPTY_STRING;

@Slf4j
@Component
public class EventRecordListener implements TargetedMessageListener {

    @Autowired
    private TraceEventRecordService traceEventRecordService;
    @Autowired
    private EventRecordService eventRecordService;

    @Override
    public String getSubdivision() {

        return EMPTY_STRING;
    }

    @Override
    public String getDestination() {

        return "event_record";
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object body = message.getBody();
            Event event = BeanUtils.beanToBean(body, Event.class);
            Map<String, Object> variables = event.getVariables();
            if (EventType.TRACE.equals(event.getType())) {
                EventObject eventObject = EventObject.create(event);
                TraceEventRecordDTO traceEventRecord =
                        BeanUtils.beanToBean(eventObject, TraceEventRecordDTO.class);
                traceEventRecordService.insertSelective(traceEventRecord);
            }
            else {
                EventRecordDTO eventRecord =
                        BeanUtils.beanToBean(event, EventRecordDTO.class);
                if (MapUtils.isNotEmpty(variables)) {
                    String json = JSON.toJSONString(variables);
                    eventRecord.setVariablesJson(json);
                }
                eventRecordService.insertSelective(eventRecord);
            }
        }
        catch (Exception e) {
            log.error("Save event record error. ", e);
        }
    }

}
