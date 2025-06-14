package br.com.lucascosta.emailservice.listeners;

import br.com.lucascosta.emailservice.services.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.dtos.OrderCreatedMessage;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static br.com.lucascosta.emailservice.models.enums.OperationEnum.ORDER_CREATED;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderListener {

    private final EmailService emailService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "helpdesk", type = "topic"),
            value = @Queue(value = "queue.orders"),
            key = "rk.orders.create"
    ))
    public void listener(final OrderCreatedMessage message) throws MessagingException {
        log.info("Order created message received: {}", message);

        emailService.sendHtmlMail(message, ORDER_CREATED);
    }
}
