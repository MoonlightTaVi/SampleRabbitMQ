package com.github.tavi.srmq.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.ListenerContainerConsumerFailedEvent;
import org.springframework.context.ApplicationListener;


// @Component
public class ConnectionErrorListener
        implements ApplicationListener<ListenerContainerConsumerFailedEvent> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ListenerContainerConsumerFailedEvent event) {
        log.warn("AMQP raised exception: {}",
                event.getThrowable().getClass().getSimpleName());
    }

}
