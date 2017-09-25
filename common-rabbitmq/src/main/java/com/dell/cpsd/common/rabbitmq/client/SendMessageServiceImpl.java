/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * Impl Class for Send Message Service. Has three overloaded messages that can be utilized to send response Message
 * 
 *
 *         <p>
 *         Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *         </p>
 */
@Service("sendMessageService")
public class SendMessageServiceImpl implements SendMessageService
{

    private static final String   DEFAULT_REPLY_TO_PLACEHOLDER = "{replyTo}";

    private final MessageProducer messageProducer;

    @Autowired
    public SendMessageServiceImpl(MessageProducer messageProducer)
    {
        this.messageProducer = messageProducer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String exchange, String replyToAddress, String responseKey,
            HasMessageProperties<? extends MessagePropertiesContainer> responseMessage) throws IllegalArgumentException
    {
        messageProducer.convertAndSend(exchange, generateRequestRoutingKey(replyToAddress, responseKey, DEFAULT_REPLY_TO_PLACEHOLDER),
                responseMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String exchange, String replyToAddress, String responseKey,
            HasMessageProperties<? extends MessagePropertiesContainer> responseMessage, String placeHolder) throws IllegalArgumentException
    {
        messageProducer.convertAndSend(exchange, generateRequestRoutingKey(replyToAddress, responseKey, placeHolder), responseMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String exchange, String responseKey, HasMessageProperties<? extends MessagePropertiesContainer> responseMessage)
    {
        messageProducer.convertAndSend(exchange, responseKey, responseMessage);
    }

    private String generateRequestRoutingKey(String replyToFromRequestMessageProperties, String responseKey, String placeHolder)
            throws IllegalArgumentException
    {
        generateRequestRoutingKeyException(replyToFromRequestMessageProperties, responseKey, placeHolder);

        if (DEFAULT_REPLY_TO_PLACEHOLDER.equalsIgnoreCase(placeHolder))
        {
            return responseKey.replace(DEFAULT_REPLY_TO_PLACEHOLDER, "." + replyToFromRequestMessageProperties);
        }
        return responseKey.replace(placeHolder, "." + replyToFromRequestMessageProperties);
    }

    private void generateRequestRoutingKeyException(String replyToFromRequestMessageProperties, String responseKey, String placeHolder)
    {
        if (StringUtils.isBlank(replyToFromRequestMessageProperties))
        {
            throw new IllegalArgumentException("replyTo from RequestMessageProperties should not be null or empty");
        }
        if (StringUtils.isBlank(responseKey))
        {
            throw new IllegalArgumentException("responseKey should not be null or empty");
        }
        if (StringUtils.isBlank(placeHolder))
        {
            throw new IllegalArgumentException("placeHolder should not be null or empty");
        }
    }

}
