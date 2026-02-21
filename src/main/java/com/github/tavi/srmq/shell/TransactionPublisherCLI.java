package com.github.tavi.srmq.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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
public class TransactionPublisherCLI {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String baseUrl = "http://localhost:8081";
    private final String url = "/srmq/api/v1/transactions/send";
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * This Spring shell command sends HTTP POST request to the server. <br>
     * A request is a money transaction (in US dollars) between two persons.
     * 
     * @param senderName   The name of the user who sends the request.
     * @param receiverName The name of the user who will receive the
     *                     transaction.
     * @param amount       The amount of the money transaction.
     */
    @Command(
            alias = "send",
            description = "Send a transaction request between two users."
            )
            public void send(
            @Option(
                    shortName = 's', longName = "sender",
                            description = "The name of the user who sends the request.", required = true
                    ) 
            String senderName, 
            @Option(
                    shortName = 'r', longName = "receiver", 
                            description = "The name of the user who will receive the transaction.", required = true
                    ) 
            String receiverName, 
            @Option(
                    shortName = 'a', longName = "amount", 
                            description = "The amount of the money transaction.", required = true
                    ) 
            int amount) {

        RequestDTO request = new RequestDTO();

        // We do not set the date of the request here;
        // It will be set by the server, when the request reaches it
        request.setSenderName(senderName);
        request.setReceiverName(receiverName);
        request.setAmount(amount);

        log.info("Sending a new HTTP request.");
        boolean success = postMessage(request); // Thread sleeps here...
        log.info("HTTP request success: {}.", success);
    }


    /**
     * Sends an HTTP POST request to the default end-point.
     * Returns the success flag of this request.
     * <br>
     * <br>
     *
     * <b>Note</b>: As of the current version, the {@code RestTemplate}
     * is used to send requests. This means that the caller thread will
     * be paused until the server responds to the request.
     * 
     * @param requestDto The data transfer object of the request.
     * @return {@code true} if the request has been sent successfully
     *         (HTTP 2xx code); {@code false} otherwise.
     */
    private boolean postMessage(RequestDTO requestDto) {
        HttpEntity<RequestDTO> request = new HttpEntity<>(requestDto);

        // The RequestDTO returned by the server will be modified
        // (The server will set the date to the request)
        ResponseEntity<RequestDTO> response = null;
        try {
            response = restTemplate
                    .postForEntity(baseUrl + url, request, RequestDTO.class);
        }
        // Internal server errors are considered exceptions
        catch (HttpServerErrorException e) {
            log.warn("HTTP request failed: {}.", e.getLocalizedMessage());
        }

        return response != null && response.getStatusCode().is2xxSuccessful();
    }

}
