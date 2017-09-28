/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import java.util.Date;
import org.junit.FixMethodOrder;
import org.junit.Test;
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

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test class for the APIs provided by SendMessageService
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(SpringRunner.class)
@Configuration
@ContextConfiguration(classes = {IntegrationTestConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SendMessageServiceIT
{
    @Autowired
    private AmqpAdmin          rabbitAdmin;

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private RabbitTemplate     rabbitTemplate;

    /**
     * Validate that message is sent to exchange when replyTo is given by the user and default placeholder ({replyTo}) is used in the
     * response routing key
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSendMessage() throws InterruptedException
    {
        TopicExchange topicExchange = new TopicExchange("testExchange");
        rabbitAdmin.declareExchange(topicExchange);

        Queue queue = new Queue("testQueue");
        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with("com.dell.cpsd.exchange.routing.key.test"));
        TestResponseMessage testResponseMessage = new TestResponseMessage();
        sendMessageService.sendMessage("testExchange", "test", "com.dell.cpsd.exchange.routing.key{replyTo}", testResponseMessage);
        Thread.sleep(5000);
        TestResponseMessage responseMsg = (TestResponseMessage) rabbitTemplate.receiveAndConvert("testQueue");
        assertNotNull(responseMsg);
        assertEquals(responseMsg.getMessageProperties().getCorrelationId(), "567890");
        assertEquals(responseMsg.getMessageProperties().getReplyTo(), "test");

    }

    /**
     * Validate that message is sent to the exchange when the actual response routing key is provided by the user itself
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSendMessageWithResponseKey() throws InterruptedException
    {
        TopicExchange topicExchange = new TopicExchange("testExchange");
        rabbitAdmin.declareExchange(topicExchange);

        Queue queue = new Queue("testQueue");
        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with("com.dell.cpsd.exchange.routing.key.test"));
        TestResponseMessage testResponseMessage = new TestResponseMessage();
        sendMessageService.sendMessage("testExchange", "com.dell.cpsd.exchange.routing.key.test", testResponseMessage);
        Thread.sleep(5000);
        TestResponseMessage responseMsg = (TestResponseMessage) rabbitTemplate.receiveAndConvert("testQueue");
        assertNotNull(responseMsg);
        assertEquals(responseMsg.getMessageProperties().getCorrelationId(), "567890");
        assertEquals(responseMsg.getMessageProperties().getReplyTo(), "test");

    }

    /**
     * Validate that message is sent to the exchange when replyTo and custom placeholder is given by the user
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSendMessageWithCustomPlaceHolder() throws InterruptedException
    {
        TopicExchange topicExchange = new TopicExchange("testExchange");
        rabbitAdmin.declareExchange(topicExchange);

        Queue queue = new Queue("testQueue");
        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with("com.dell.cpsd.exchange.routing.key.test"));
        TestResponseMessage testResponseMessage = new TestResponseMessage();
        sendMessageService.sendMessage("testExchange", "test", "com.dell.cpsd.exchange.routing.key{customPlaceHolder}",
                testResponseMessage, "{customPlaceHolder}");
        Thread.sleep(5000);
        TestResponseMessage responseMsg = (TestResponseMessage) rabbitTemplate.receiveAndConvert("testQueue");
        assertNotNull(responseMsg);
        assertEquals(responseMsg.getMessageProperties().getCorrelationId(), "567890");
        assertEquals(responseMsg.getMessageProperties().getReplyTo(), "test");

    }

}

class TestResponseMessage implements HasMessageProperties<MessagePropertiesContainer>
{

    @Override
    public MessagePropertiesContainer getMessageProperties()
    {
        return new TestMessage();
    }

    @Override
    public void setMessageProperties(MessagePropertiesContainer messageProperties)
    {
    }
}

class TestMessage implements MessagePropertiesContainer
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