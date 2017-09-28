/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * The class contains the Junit test cases for MessageProducer class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageProducerImplTest
{
    @Mock
    private RabbitTemplate                                   rabbitTemplate;

    @InjectMocks
    MessageProducerImpl                                      classUnderTest;

    private String                                           requestExchangeName;

    private String                                           routingKey;

    private HasMessageProperties<MessagePropertiesContainer> requestMessage;

    /**
     * Sets the up the data required before each test case is run.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.classUnderTest = new MessageProducerImpl();
        this.requestMessage = createMessagePropertiesObject();
        this.routingKey = "routingKey";
        this.requestExchangeName = "MessageTopicExchange";
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Invoked after each test case has been run.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.classUnderTest = null;
        this.requestMessage = null;
        this.routingKey = null;
        this.requestExchangeName = null;
    }

    /**
     * Test Convert and Send funtionality of Message producer. Happy path validation for Convert and Send funtionality of Message producer..
     *
     */
    @Test
    public void testConvertAndSend()
    {
        Mockito.doNothing().when(this.rabbitTemplate).convertAndSend(requestExchangeName, routingKey, this.requestMessage);
        this.classUnderTest.convertAndSend(requestExchangeName, routingKey, this.requestMessage);
        Mockito.verify(this.rabbitTemplate, Mockito.atLeastOnce()).convertAndSend(requestExchangeName, routingKey, this.requestMessage);
    }

    /**
     * Test convert and send with routing key null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertAndSendWithRoutingKeyNull()
    {
        this.classUnderTest.convertAndSend(requestExchangeName, null, this.requestMessage);
    }

    /**
     * Test convert and send with request exchange null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertAndSendWithRequestExchangeNull()
    {
        this.classUnderTest.convertAndSend(null, routingKey, this.requestMessage);
    }

    /**
     * Test convert and send with request message null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertAndSendWithRequestMessageNull()
    {
        this.classUnderTest.convertAndSend(requestExchangeName, routingKey, null);
    }

    /**
     * Creates the message properties object.
     *
     * @return HasMessageProperties<MessagePropertiesContainer> object.
     */
    private HasMessageProperties<MessagePropertiesContainer> createMessagePropertiesObject()
    {
        HasMessageProperties<MessagePropertiesContainer> messageProperty = new HasMessageProperties<MessagePropertiesContainer>()
        {
            private MessagePropertiesContainer messageProperties = new MessagePropertiesContainer()
            {
                private String correlationId;

                @Override
                public void setTimestamp(Date arg0)
                {
                }

                @Override
                public Date getTimestamp()
                {
                    return null;
                }

                @Override
                public void setReplyTo(String arg0)
                {
                }

                @Override
                public String getReplyTo()
                {
                    return null;
                }

                @Override
                public void setCorrelationId(String arg0)
                {
                }

                @Override
                public String getCorrelationId()
                {
                    return this.correlationId;
                }
            };

            @Override
            public MessagePropertiesContainer getMessageProperties()
            {
                return this.messageProperties;
            }

            @Override
            public void setMessageProperties(MessagePropertiesContainer messageProperties)
            {
                this.messageProperties = messageProperties;

            }

        };
        return messageProperty;
    }
}
