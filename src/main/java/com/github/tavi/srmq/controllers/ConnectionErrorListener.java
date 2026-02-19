package com.github.tavi.srmq.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.ListenerContainerConsumerFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * The Logback logger is configured to log into separate files;
 * AMQP has its own log file with detailed messages.<br>
 * This component will intercept any AMQP exceptions and log them as
 * the application exceptions, but without any details.
 */
@Component
public class ConnectionErrorListener
        implements ApplicationListener<ListenerContainerConsumerFailedEvent> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ListenerContainerConsumerFailedEvent event) {
        log.warn("AMQP raised exception: {}",
                event.getThrowable().getClass().getSimpleName());
    }

}
