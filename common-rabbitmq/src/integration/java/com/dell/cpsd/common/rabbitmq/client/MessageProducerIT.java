/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * Integration test class for the APIs provided by MessageProducerService
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(SpringRunner.class)
@Configuration
@ContextConfiguration(classes = {IntegrationTestConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageProducerIT
{
    @Autowired
    private AmqpAdmin       rabbitAdmin;

    @Autowired
    private RabbitTemplate  rabbitTemplate;

    @Autowired
    private MessageProducer messageProducer;

    /**
     * Validate that message is sent to the exchange.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testMessageProducer() throws InterruptedException
    {
        TopicExchange topicExchange = new TopicExchange("testExchange");
        rabbitAdmin.declareExchange(topicExchange);

        Queue queue = new Queue("testQueue");
        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with("com.dell.cpsd.exchange.routing.key.test"));
        TestRequestMessage testRequestMessage = new TestRequestMessage();

        messageProducer.convertAndSend("testExchange", "com.dell.cpsd.exchange.routing.key.test", testRequestMessage);
        Thread.sleep(5000);
        TestRequestMessage responseMsg = (TestRequestMessage) rabbitTemplate.receiveAndConvert("testQueue");
        assertNotNull(responseMsg);
        assertEquals(responseMsg.getMessageProperties().getCorrelationId(), "567890");
        assertEquals(responseMsg.getMessageProperties().getReplyTo(), "test");

    }

}

class TestRequestMessage implements HasMessageProperties<MessagePropertiesContainer>
{

    @Override
    public MessagePropertiesContainer getMessageProperties()
    {
        return new TestMessageProperties();
    }

    @Override
    public void setMessageProperties(MessagePropertiesContainer messageProperties)
    {
    }
}

class TestMessageProperties implements MessagePropertiesContainer
{

    @Override
    public String getCorrelationId()
    {
        return "567890";
    }

    @Override
    public void setCorrelationId(String correlationId)
    {
    }

    @Override
    public String getReplyTo()
    {
        return "test";
    }

    @Override
    public void setReplyTo(String replyTo)
    {
    }

    @Override
    public Date getTimestamp()
    {
        return null;
    }

    @Override
    public void setTimestamp(Date timestamp)
    {
    }
}
