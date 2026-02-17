package com.github.tavi.srmq.shell;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import com.github.tavi.srmq.dto.RequestDTO;


@Component
public class MessagePublisherCLI {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RabbitTemplate rabbit;


    @Command(
            alias = "send",
            description = "Send a transaction request between two users."
            )
    public String send(
            @Option(
                    shortName = 's', longName = "sender",
                    description = "The name of the user who sends the request."
                    ) 
            String senderName, 
            @Option(
                    shortName = 'r', longName = "receiver", 
                    description = "The name of the user who will receive the transaction."
                    ) 
            String receiverName, 
            @Option(
                    shortName = 'a', longName = "amount", 
                    description = "The amount of the money transaction."
                    ) 
            int amount) {

        RequestDTO request = new RequestDTO();

        request.setSenderName(senderName);
        request.setReceiverName(receiverName);
        request.setAmount(amount);

        request.setDate(new Date(System.currentTimeMillis()));

        String result = "The request has been sent.";
        try {
            rabbit.convertAndSend("srmq.transactions.events", request);
        } catch (AmqpException e) {
            result = String.format("Could not publish the message:%n \t%s.",
                    e.getLocalizedMessage());
            log.error("Message could not be published - {}", e);
        }

        return result;
    }


}
