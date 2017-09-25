/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * MessageProducerImpl provides Api's to send the messages to the message bus.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Service("messageProducer")
public class MessageProducerImpl implements MessageProducer
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public void convertAndSend(String exchangeName, String routingKey, HasMessageProperties<? extends MessagePropertiesContainer> message)
    {
        Assert.notNull(message, "Message cannot be empty");
        Assert.hasText(routingKey, "Routing key cannot be empty");
        Assert.hasText(exchangeName, "Exchange name cannot be empty");
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

}
