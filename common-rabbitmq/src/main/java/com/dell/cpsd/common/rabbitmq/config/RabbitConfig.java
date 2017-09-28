/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Configuration for the RabbitMQ artifacts used by the service.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Configuration
public class RabbitConfig
{

    private static final Logger LOGGER           = LoggerFactory.getLogger(RabbitConfig.class);
    private static final int    MAX_ATTEMPTS     = 10;
    private static final int    INITIAL_INTERVAL = 100;
    private static final double MULTIPLIER       = 2.0;
    private static final int    MAX_INTERVAL     = 50000;

    @Autowired
    @Qualifier("rabbitConnectionFactory")
    private ConnectionFactory   rabbitConnectionFactory;

    @Autowired
    @Qualifier("rabbitPropertiesConfig")
    private PropertiesConfig    propertiesConfig;

    /**
     * create bean for rabbitTemplate
     * 
     * @return {@link rabbitTemplate}
     */
    @Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setMessageConverter(messageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    /**
     * create bean for retryTemplate
     * 
     * @return {@link RetryTemplate}
     */
    @Bean
    public RetryTemplate retryTemplate()
    {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        LOGGER.debug("Retry configuration: " + backOffPolicy + " / " + retryPolicy);

        return retryTemplate;
    }

    /**
     * This message converter is used by the message listener container to serialize and deserialize the messages. It also helps in
     * retaining the correct object type for non-final classes.
     * 
     * @return {@link MessageConverter}
     */
    @Bean
    public MessageConverter messageConverter()
    {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.setClassMapper(classMapper());
        messageConverter.setCreateMessageIds(true);

        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // use ISO8601 format for dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        messageConverter.setJsonObjectMapper(objectMapper);

        // ignore properties we don't need or aren't expecting
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        messageConverter.setJsonObjectMapper(objectMapper);

        return messageConverter;
    }

    /**
     * Create bean for AmqpAdmin
     * 
     * @return {@link AmqpAdmin}
     */
    @Bean
    @Qualifier("rabbitConfigAmqpAdmin")
    public AmqpAdmin amqpAdmin()
    {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    /**
     * create bean for DefaultClassMapper
     * 
     * @return {@link DefaultClassMapper}
     */
    @Bean
    public DefaultClassMapper classMapper()
    {
        return new DefaultClassMapper();
    }

    /**
     * create bean for hostName
     * 
     * @return {@link String}
     */
    @Bean
    public String hostName()
    {
        try
        {
            return System.getProperty("container.id");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to identify containerId", e);
        }
    }

    @Bean
    public String replyTo()
    {
        return propertiesConfig.applicationName() + "." + hostName();
    }

    /**
     * create bean for responseQueue
     * 
     * @return {@link Queue}
     */
    // TODO: To be moved to capability execution starter
    @Bean
    public Queue responseQueue()
    {
        return new Queue(propertiesConfig.responseQueueName() + "." + replyTo());
    }

}
