package br.com.lucascosta.emailservice.services;

import br.com.lucascosta.emailservice.models.enums.OperationEnum;
import br.com.lucascosta.emailservice.utils.EmailUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.dtos.OrderCreatedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.username}")
    private String mailUsername;

    public void sendHtmlMail(
            final OrderCreatedMessage orderDTO, OperationEnum operation
    ) throws MessagingException {

        var message = mailSender.createMimeMessage();
        var process = getContext(orderDTO, operation);

        var subject = switch (operation) {
            case ORDER_CREATED -> "Service Order Created Successfully";
            case ORDER_UPDATED -> "Service Order Updated";
            case ORDER_DELETED -> "Service Order Deleted";
        };

        EmailUtils.getMimeMessage(message, process, orderDTO, subject, mailUsername);

        mailSender.send(message);
    }

    private String getContext(OrderCreatedMessage orderDTO, OperationEnum operation) {
        return switch (operation) {

            case ORDER_CREATED -> {
                log.info("Sending service order creation email");

                var context = EmailUtils.getContext(orderDTO);
                yield templateEngine.process("email/order-created", context);
            }

            case ORDER_UPDATED -> {
                log.info("Sending service order update email");

                var context = EmailUtils.getContext(orderDTO);
                yield templateEngine.process("email/order-updated", context);
            }

            case ORDER_DELETED -> {
                log.info("Sending service order deletion email");

                var context = EmailUtils.getContext(orderDTO);
                yield templateEngine.process("email/order-deleted", context);
            }
        };
    }

}