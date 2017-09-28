/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(MockitoJUnitRunner.class)
public class SendMessageServiceImplTest
{

    @InjectMocks
    private SendMessageServiceImpl                                     classUnderTest;

    private String                                                     exchange;
    private String                                                     replyToAddress;
    private String                                                     responseKey;
    private String                                                     placeHolder;

    @Mock
    private MessageProducer                                            messageProducer;

    @Mock
    private HasMessageProperties<? extends MessagePropertiesContainer> responseMessage;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        exchange = "exchange.dell.cpsd.sample";
        replyToAddress = "project-system_name";
        responseKey = "dell.cpsd.sample.list.storage.response{replyTo}";
        placeHolder = "{changeMe}";
        responseMessage = new HasMessageProperties<MessagePropertiesContainer>()
        {

            @Override
            public MessagePropertiesContainer getMessageProperties()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setMessageProperties(MessagePropertiesContainer messageProperties)
            {
                // TODO Auto-generated method stub
                
            }
        };
    }

    /**
     * Positive - Validate Send Message (Method One) to send a valid response
     * 
     * @throws IllegalArgumentException
     */
    @Test
    public void testSendMessage_WithValidScenarioMethodOne() throws IllegalArgumentException
    {
        Mockito.doNothing().when(messageProducer)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(HasMessageProperties.class));
        classUnderTest.sendMessage(exchange, replyToAddress, responseKey, responseMessage);
    }

    /**
     * Negative - Validate Send Message (Method One) triggers Illegal Argument Exception when replyTo is null
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageForNull_replyToAddressMethodOne() throws IllegalArgumentException
    {

        classUnderTest.sendMessage(exchange, null, responseKey, responseMessage);
    }

    /**
     * Negative - Validate Send Message (Method One) triggers Null Pointer Exception when response key is null
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageForNull_responseKeyMethodOne() throws IllegalArgumentException
    {
        classUnderTest.sendMessage(exchange, replyToAddress, null, responseMessage);
    }

    /**
     * Positive - Validate Send Message (Method Two) to send a valid response
     * 
     * @throws IllegalArgumentException
     */
    @Test
    public void testSendMessage_WithValidScenarioMethodTwo() throws IllegalArgumentException
    {
        Mockito.doNothing().when(messageProducer)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(HasMessageProperties.class));
        classUnderTest.sendMessage(exchange, replyToAddress, responseKey, responseMessage, placeHolder);
    }

    /**
     * Negative - Validate Send Message (Method Two) triggers Illegal Argument Exception when replyTo is null
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageForNull_replyToAddressMethodTwo() throws IllegalArgumentException
    {
        classUnderTest.sendMessage(exchange, null, responseKey, responseMessage, placeHolder);
    }

    /**
     * Negative - Validate Send Message (Method Two) triggers Null Pointer Exception when response key is null
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageForNull_responseKeyMethodTwo() throws IllegalArgumentException
    {
        classUnderTest.sendMessage(exchange, replyToAddress, null, responseMessage, placeHolder);
    }

    /**
     * Negative - Validate Send Message (Method Two) triggers Null Pointer Exception when placeHolder is null
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageForNull_placeHolderMethodTwo() throws IllegalArgumentException
    {
        classUnderTest.sendMessage(exchange, replyToAddress, responseKey, responseMessage, null);
    }

    /**
     * Positive - Validate Send Message (Method Three) to send a valid response
     * 
     * @throws IllegalArgumentException
     */
    @Test
    public void testSendMessage_WithValidScenarioMethodThree() throws IllegalArgumentException
    {
        Mockito.doNothing().when(messageProducer)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(HasMessageProperties.class));
        classUnderTest.sendMessage(exchange, responseKey, responseMessage);
    }

}
