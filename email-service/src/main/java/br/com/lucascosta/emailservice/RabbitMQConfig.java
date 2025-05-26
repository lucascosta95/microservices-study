package br.com.lucascosta.emailservice;

import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Bean
    public SimpleMessageConverter messageConverter() {
        var messageConverter = new SimpleMessageConverter();
        messageConverter.setAllowedListPatterns(List.of("models.*", "java.util.*", "java.lang.*", "java.time.*"));
        return messageConverter;
    }
}
