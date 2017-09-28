/*
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.dell.cpsd.common.rabbitmq.log.RabbitMQMessageCode;
import com.dell.cpsd.common.rabbitmq.processor.PropertiesPostProcessor;

/**
 * AbstractAmqpMessageProducer Test.
 * <p>
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class AbstractAmqpMessageProducerTest
{

    ConcreteAbstractAmqpProducerTest concreteAmqpProducer;
    @Mock
    Exchange                         exchange;
    @Rule
    public ExpectedException         thrown       = ExpectedException.none();
    private static final String      EXHCANGE_KEY = "dummyExchangeName";
    @Mock
    RabbitTemplate                   rabbitTemplate;

    @Before
    public void setUp()
    {
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        concreteAmqpProducer = new ConcreteAbstractAmqpProducerTest(rabbitTemplate, "dummyHostName");
        exchange = Mockito.mock(Exchange.class);
    }

    @Test
    public void testAddExchange()
    {
        concreteAmqpProducer.addExchange(EXHCANGE_KEY, exchange);
        assertTrue(concreteAmqpProducer.getExchanges().containsKey(EXHCANGE_KEY));
        assertEquals(exchange, concreteAmqpProducer.getExchanges().get(EXHCANGE_KEY));
    }

    @Test
    public void testAddExchangeNullName()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The exchange name is not set.");
        concreteAmqpProducer.addExchange(null, exchange);
    }

    @Test
    public void testAddExchangeNullExchange()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The exchange is not set.");
        concreteAmqpProducer.addExchange(EXHCANGE_KEY, null);
    }

    @Test
    public void testAddRemoveExchange()
    {
        concreteAmqpProducer.addExchange(EXHCANGE_KEY, exchange);
        assertTrue(concreteAmqpProducer.getExchanges().containsKey(EXHCANGE_KEY));
        assertEquals(exchange, concreteAmqpProducer.getExchanges().get(EXHCANGE_KEY));

        Exchange exchange1 = concreteAmqpProducer.removeExchange(EXHCANGE_KEY);
        assertTrue(!(exchange1 == null));

        Exchange exchange2 = concreteAmqpProducer.removeExchange("noKey");
        assertTrue(exchange2 == null);
    }

    @Test
    public void testLookUpExchange()
    {
        concreteAmqpProducer.addExchange(EXHCANGE_KEY, exchange);
        assertEquals(exchange, concreteAmqpProducer.lookupExchange(EXHCANGE_KEY));
    }

    @Test
    public void testSetHostNameNull()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The host name is not set.");
        concreteAmqpProducer.setHostname(null);
    }

    @Test
    public void testSetRabbitTemplateNull()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The rabbit template is not set.");
        concreteAmqpProducer.setRabbitTemplate(null);
    }

    @Test
    public void testPublishMessageException()
    {
        String noExchangeKey = "noExchangeKey";
        thrown.expect(AmqpException.class);
        thrown.expectMessage(RabbitMQMessageCode.NO_EXCHANGE_FOUND_E.getMessageCode() + " No exchange was found with the name ["
                + noExchangeKey + "]");
        concreteAmqpProducer.publishMessage("dummyCorrelationId", "dummyReplyTo", "noExchangeKey", "noRoutingKey", new Object());
    }

    @Test
    public void testPublishMessage()
    {
        concreteAmqpProducer.addExchange(EXHCANGE_KEY, exchange);
        String correlationId = "dummyCorrelationId";
        String replyTo = "dummyReplyTo";

        Mockito.when(exchange.getName()).thenReturn("exchangeName");
        concreteAmqpProducer.publishMessage(correlationId, replyTo, EXHCANGE_KEY, "dummyRoutingKey", new Object());
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class), any(PropertiesPostProcessor.class));

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyObject(), any(PropertiesPostProcessor.class));
    }

}