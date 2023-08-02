package com.koray.atmproject.service;

import com.koray.atmproject.dto.KafkaMessage;
import com.koray.atmproject.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public KafkaMessage getKafkaMessageFromAccounts(Account accountOfSender, Account accountOfReceiver) {
        logger.info("getKafkaMessageFromAccounts started");
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setSenderId(accountOfSender.getAccountId());
        kafkaMessage.setSenderAccountNumber(accountOfSender.getAccountNumber());
        kafkaMessage.setSenderAmount(accountOfSender.getAmount());
        kafkaMessage.setSenderUserId(accountOfSender.getUserInfo().getUserId());
        kafkaMessage.setSenderUserName(accountOfSender.getUserInfo().getName());
        kafkaMessage.setSenderUserEmail(accountOfSender.getUserInfo().getEmail());

        kafkaMessage.setReceiverId(accountOfReceiver.getAccountId());
        kafkaMessage.setReceiverAccountNumber(accountOfReceiver.getAccountNumber());
        kafkaMessage.setReceiverAmount(accountOfReceiver.getAmount());
        kafkaMessage.setReceiverUserId(accountOfReceiver.getUserInfo().getUserId());
        kafkaMessage.setReceiverUserName(accountOfReceiver.getUserInfo().getName());
        kafkaMessage.setReceiverUserEmail(accountOfReceiver.getUserInfo().getEmail());
        logger.info("getKafkaMessageFromAccounts returned kafkaMessage");
        return kafkaMessage;
    }

    @KafkaListener(topics = "money_transfer", groupId = "group-id")
    public void consumeMessage(KafkaMessage kafkaMessage) {

        logger.info(String.format("Message consumed -> %s",kafkaMessage));

    }


}
