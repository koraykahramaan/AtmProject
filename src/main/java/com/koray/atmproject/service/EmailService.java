package com.koray.atmproject.service;

import com.koray.atmproject.dto.KafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String emailFrom;

    public void sendEmail(String emailTo, KafkaMessage kafkaMessage) {
        logger.info("Send email process started.");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject("Money Transfer Transaction ");

        String message = "Your money transfer ended successfully.\n\n" +
                "Transaction Amount : " + kafkaMessage.getAmount() + "\n" +
                "Sender account number : " + kafkaMessage.getReceiverAccountNumber() + "\n" +
                "Receiver Account Number : " + kafkaMessage.getReceiverAccountNumber() + "\n" +
                "Your new amount : " + kafkaMessage.getSenderAmount() + "\n\n" +
                "We wish you a good day.";
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
        logger.info("Email sent successfully to : {}",kafkaMessage.getSenderUserEmail());
    }

}
