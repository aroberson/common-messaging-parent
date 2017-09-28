/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dell.cpsd.common.rabbitmq.config.RabbitMQPropertiesConfig;

/**
 * The configuration for the client.This class will fetch the rabbitmq properties from the properties file and creates a bean out of it.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Configuration
@Qualifier("rabbitPropertiesConfig")
public class PropertiesConfig extends RabbitMQPropertiesConfig
{

    /**
     * create bean for response queue name
     * 
     * @return queue name provided by the user
     */
    @Bean
    public String responseQueueName()
    {
        return environment.getProperty("queue.dell.cpsd.response.name", "");
    }

    /**
     * create bean for response exchange name
     * 
     * @return exchange name provided by the user
     */
    @Bean
    public String responseExchange()
    {
        return environment.getProperty("exchange.dell.cpsd.response.name", "");
    }

}
