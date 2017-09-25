/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * MessageProducer - Interface for message producing related api's. Api's to send message to the message bus.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public interface MessageProducer
{

    /**
     * Invokes the convertAndSend() method of {@link RabbitTemplate} that converts the object to Message object and sends.
     * 
     * @param message
     *            {@link HasMessageProperties} - request message
     * @param routingKey
     *            {@link String} - routing key
     * @param exchangeName
     *            {@link String} - exchange name
     * @exception IllegalArgumentException
     *                {@link IllegalArgumentException} - if the parameters passed are null/empty.
     */
    void convertAndSend(String exchangeName, String routingKey, HasMessageProperties<? extends MessagePropertiesContainer> message);
}
