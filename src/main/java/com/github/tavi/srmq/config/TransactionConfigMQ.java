package com.github.tavi.srmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration and bean factory for RabbitMQ services.
 */
@Configuration
@EnableRabbit
public class TransactionConfigMQ {

    private final String topicExchangeName = "transactions.exchange";
    private final String queueName = "transactions.queue";
    private final String routingKey = "srmq.transactions.*";


    @Bean
    public Queue queue() {
        // False here means "reset after server restart"
        return new Queue(queueName, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }


    /**
     * The application uses data POJOs as messages
     * (transfered as JSON objects).
     * 
     * @return MessageConverter for JSON objects.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Rabbit template is used to send Rabbit queries.
     * 
     * @param connectionFactory    It will be created by Spring context
     *                             automatically.
     * @param jsonMessageConverter This converter is used to convert between
     *                             POJO and JSON.
     * @return The default rabbit template for JSON queries.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbit = new RabbitTemplate(connectionFactory);
        rabbit.setMessageConverter(jsonMessageConverter);
        return rabbit;
    }
}
