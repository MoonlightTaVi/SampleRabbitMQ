package com.github.tavi.srmq.controllers;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.tavi.srmq.dto.RequestDTO;


/**
 * This component is a simple example of a Rabbit listener;
 * it will log the received messages to the main application log file
 * (which may imitate the logic of working with a database, for example).
 */
@Component
public class TransactionListener {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SimpleDateFormat format;


    public TransactionListener() {
        format = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss");
    }


    @RabbitListener(queues = "transactions.queue")
    public void receive(@Payload RequestDTO request) {
        log.info("Message received: {} has sent ${} to {} at {}.",
                request.getSenderName(), request.getAmount(),
                request.getReceiverName(), format.format(request.getDate()));
    }

}
