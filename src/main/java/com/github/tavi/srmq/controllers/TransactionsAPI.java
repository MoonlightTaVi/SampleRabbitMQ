package com.github.tavi.srmq.controllers;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tavi.srmq.dto.RequestDTO;


/**
 * The REST controller of the server. <br>
 * It receives the requests of money transactions from web clients
 * and schedules the respectful messages to the RabbitMQ service.
 */
@RestController
@RequestMapping("srmq/api/v1/transactions")
public class TransactionsAPI {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RabbitTemplate rabbit;


    /**
     * Receives a POST request of a money transaction between two persons. <br>
     * Updates the date of the request, so that the web-client may know
     * when the request has reached the server.
     * 
     * @param request Data Transfer Object that contains the required
     *                information.
     * @return The same (modified) DTO and HTTP 202 status, if successful;
     *         the empty response and HTTP 500 otherwise.
     */
    @PostMapping("send")
    public ResponseEntity<RequestDTO> postMessage(
            @RequestBody RequestDTO request)
    {
        request.setDate(new Date(System.currentTimeMillis()));

        ResponseEntity<RequestDTO> response = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        try {
            log.info("Received message.");

            rabbit.convertAndSend("transactions.exchange",
                    "srmq.transactions.events", request);
            response = ResponseEntity.ok(request);

            log.info("The message has been published.");
        } catch (AmqpException e) {
            log.warn("The message could not be published - {}",
                    e.getLocalizedMessage());
        }

        return response;
    }

}
