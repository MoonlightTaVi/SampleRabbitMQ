package com.github.tavi.srmq.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.tavi.srmq.dto.RequestDTO;


@Component
public class TransactionListener {

    @RabbitListener(queues = "transactions.queue")
    public void receive(@Payload RequestDTO request) {
        // TODO Write actual RabbitMQ message receiving logic
        System.out.println(request);
    }

}
