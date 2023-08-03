package com.koray.atmproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KafkaMessage {

    private int senderId;
    private int senderAmount;
    private String senderAccountNumber;
    private int senderUserId;
    private String senderUserName;
    private String senderUserEmail;
    private int receiverId;
    private int receiverAmount;
    private String receiverAccountNumber;
    private int receiverUserId;
    private String receiverUserName;
    private String receiverUserEmail;
    private int amount;

}
