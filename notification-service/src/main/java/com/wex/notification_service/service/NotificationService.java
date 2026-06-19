package com.wex.notification_service.service;

import com.wex.notification_service.model.OrderRequest;
import com.wex.notification_service.model.PaymentRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseEntity<String> sendMailAboutOrder(OrderRequest orderRequest) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("kiril102836547@gmail.com");
            helper.setTo(orderRequest.getNotification().getSendToEmail());
            helper.setSubject(orderRequest.getNotification().getSubject());

            try(var inputStream = Objects.requireNonNull(
                    NotificationService.class.getResourceAsStream("/templates/email-context-order.html"))){

                String address = orderRequest.getShippingAddress().getStreet() + " " + orderRequest.getShippingAddress().getCity() + " " + orderRequest.getShippingAddress().getCountry();
                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                html = html
                        .replace("{{orderId}}", String.valueOf(orderRequest.getOrderId()))
                        .replace("{{status}}", orderRequest.getStatus().toString())
                        .replace("{{price}}", String.valueOf(orderRequest.getPrice()))
                        .replace("{{address}}", address)
                        .replace("{{message}}", orderRequest.getNotification().getText());


                helper.setText(html, true);
                javaMailSender.send(mimeMessage);
                return new ResponseEntity<>("success", HttpStatus.OK);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> sendMailAboutPayment(PaymentRequest paymentRequest) {
        System.out.println("SENDING PAYMENT");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("kiril102836547@gmail.com");
            helper.setTo(paymentRequest.getNotification().getSendToEmail());
            helper.setSubject(paymentRequest.getNotification().getSubject());

            try(var inputStream = Objects.requireNonNull(
                    NotificationService.class.getResourceAsStream("/templates/email-context-payment.html"))){
                System.out.println("SENDING HTML BODY");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                String date = paymentRequest.getPaymentDate().format(formatter);

                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                html = html
                        .replace("{{orderId}}", String.valueOf(paymentRequest.getOrderId()))
                        .replace("{{status}}", paymentRequest.getStatus().toString())
                        .replace("{{price}}", String.valueOf(paymentRequest.getAmount()))
                        .replace("{{paymentDate}}", date)
                        .replace("{{message}}", paymentRequest.getNotification().getText());


                helper.setText(html, true);
                javaMailSender.send(mimeMessage);
                System.out.println("EMAIL SENT");
                return new ResponseEntity<>("success", HttpStatus.OK);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
