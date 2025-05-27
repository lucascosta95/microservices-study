package br.com.lucascosta.emailservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.dtos.OrderCreatedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.text-created-order-confirmation}")
    private String textCreatedOrderConfirmation;

    public void sendEmail(final OrderCreatedMessage orderCreatedMessage) {
        log.info(
                "Sending email for client: {}, order: {}",
                orderCreatedMessage.getCustomer().email(), orderCreatedMessage.getOrder().title()
        );

        var message = getSimpleMailMessage(orderCreatedMessage);

        try {
            mailSender.send(message);
            log.info(
                    "Email sent successfully for client: {}, order: {}",
                    orderCreatedMessage.getCustomer().email(), orderCreatedMessage.getOrder().title()
            );
        } catch (MailException e) {
            switch (e.getClass().getSimpleName()) {
                case "MailAuthenticationException":
                    log.error("Authentication failed while sending email: {}", e.getMessage());
                    break;
                case "MailSendException":
                    log.error("Failed to send email: {}", e.getMessage());
                    break;
                case "MailParseException":
                    log.error("Error parsing email: {}", e.getMessage());
                    break;
                default:
                    log.error("An unexpected error occurred while sending email: {}", e.getMessage());
            }
        }
    }

    private SimpleMailMessage getSimpleMailMessage(OrderCreatedMessage orderCreatedMessage) {
        var text = String.format(
                textCreatedOrderConfirmation,
                orderCreatedMessage.getCustomer().name(),
                orderCreatedMessage.getOrder().id(),
                orderCreatedMessage.getOrder().title(),
                orderCreatedMessage.getOrder().description(),
                orderCreatedMessage.getOrder().createAt(),
                orderCreatedMessage.getOrder().status(),
                orderCreatedMessage.getRequester().name()
        );

        var subject = "Order created with success!";
        var message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setTo(orderCreatedMessage.getCustomer().email());
        message.setText(text);
        return message;
    }
}
