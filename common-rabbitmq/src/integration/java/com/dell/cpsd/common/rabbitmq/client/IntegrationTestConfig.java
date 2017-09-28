/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.dell.cpsd.common.rabbitmq.config.ConsumerConfig;
import com.dell.cpsd.common.rabbitmq.config.IRabbitMqPropertiesConfig;
import com.dell.cpsd.common.rabbitmq.config.PropertiesConfig;
import com.dell.cpsd.common.rabbitmq.config.RabbitConfig;
import com.dell.cpsd.common.rabbitmq.config.RabbitMQPropertiesConfig;
import com.dell.cpsd.common.rabbitmq.config.RabbitMqProductionConfig;
import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQCachingConnectionFactory;
import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQTLSFactoryBean;
import com.dell.cpsd.common.rabbitmq.registration.notifier.config.RegistrationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The class contains all the related configurations required to run integration tests
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Configuration
@ComponentScan(basePackages = {"com.dell.cpsd.common.rabbitmq.client"})
@PropertySources({@PropertySource("classpath:rabbitmq.properties")})
@Import({RabbitConfig.class, PropertiesConfig.class,ConsumerConfig.class, RabbitMqProductionConfig.class,RegistrationConfig.class})
public class IntegrationTestConfig
{
    /*
     * The configuration properties for the client.
     */
    @Autowired
    private IRabbitMqPropertiesConfig propertiesConfig;

    /**
     * @return The <code>ConnectionFactory</code> to use.
     * @since SINCE-TBD TODO: Reuse the config from RabbitMqProductionConfig. Currently that bean is set to production profile and hence
     *        cannot be used.
     */
    @Bean
    @Qualifier("rabbitConnectionFactory")
    public ConnectionFactory productionCachingConnectionFactory()
    {
        RabbitMQCachingConnectionFactory cachingCF = null;
        com.rabbitmq.client.ConnectionFactory connectionFactory;

        try
        {
            if (propertiesConfig.isSslEnabled())
            {
                RabbitMQTLSFactoryBean rabbitMQTLSFactoryBean = new RabbitMQTLSFactoryBean(propertiesConfig);
                connectionFactory = rabbitMQTLSFactoryBean.getObject();
            }
            else
            {
                connectionFactory = new com.rabbitmq.client.ConnectionFactory();
            }

            cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return cachingCF;
    }

    /**
     * create bean for ObjectMapper
     * 
     * @return {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}
