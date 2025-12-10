package uiharu.common;

import artoria.message.MessageProvider;
import artoria.message.RedisMessageProvider;
import artoria.message.SimpleMessageAutoConfiguration;
import artoria.message.SimpleMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * 事件记录通道配置.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter(SimpleMessageAutoConfiguration.class)
public class EventRecordAutoConfiguration {

    @Autowired
    public EventRecordAutoConfiguration(MessageProvider messageProvider) {
        if (messageProvider instanceof SimpleMessageProvider) {
            SimpleMessageProvider simpleMessageProvider = (SimpleMessageProvider) messageProvider;
            simpleMessageProvider.createQueue("event_record");
        }
        else if (messageProvider instanceof RedisMessageProvider) {
            RedisMessageProvider redisMessageProvider = (RedisMessageProvider) messageProvider;
            redisMessageProvider.createQueue("event_record");
        }
    }

}
