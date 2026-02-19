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


/**
 * This class is a Spring Shell component that may be executed from
 * a command line. It manages the message publishing.
 * <br>
 * <br>
 * 
 * Because of the fact that the application serves as a web-server,
 * a message listener and a message publisher at the same time,
 * the shell <b>cannot</b> be used in the interactive mode;
 * the only way to use it is as a script
 * (by passing the required arguments to CMD when launching the application).
 * <br>
 * <br>
 * 
 * Though, it is always possible to move this logic into another JAR
 * that will run in a different process.
 */
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

        String result = "<shell>: The request has been sent.";
        try {
            rabbit.convertAndSend("transactions.exchange",
                    "srmq.transactions.events", request);
        } catch (AmqpException e) {
            result = "<shell>: Could not publish the message.";
            log.error("Message could not be published - {}",
                    e.getLocalizedMessage());
        }

        return result;
    }


}
