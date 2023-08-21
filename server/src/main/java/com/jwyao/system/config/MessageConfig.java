package com.jwyao.system.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    /**
     * 扩展MQ消息队列的消息转换器
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        // 配置JSON转换器，替换默认的JDK序列化
        return new Jackson2JsonMessageConverter();
    }

}
