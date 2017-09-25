/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.client;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;

/**
 * Send Message Service, has three overloaded messages that can be utilized to send response Message
 * 
 *
 *         <p>
 *         Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *         </p>
 */
public interface SendMessageService
{

    /**
     * This method will replace "{replyTo}" in responseKey with the replyToAddress Send the message to the message bus
     * 
     * @param exchange
     *            - {@link String} - Response Exchange of Consumer
     * @param replyToAddress
     *            - {@link String} - Reply To Address of Client
     * @param responseKey
     *            - {@link String} - Response Key of Consumer - expected to have {replyTo} that needs to be replaced
     * @param responseMessage
     *            - {@link HasMessageProperties} - Response Message to be sent to Client
     * @throws IllegalArgumentException
     *             - Throws {@link IllegalArgumentException} if <b>responseKey</b> or <b>replyToAddress</b> is null
     */
    void sendMessage(String exchange, String replyToAddress, String responseKey,
            HasMessageProperties<? extends MessagePropertiesContainer> responseMessage) throws IllegalArgumentException;

    /**
     * This method will replace placeHolder in responseKey with the replyToAddress Send the message to the message bus
     * 
     * @param exchange
     *            - {@link String} - Response Exchange of Consumer
     * @param replyToAddress
     *            - {@link String} - Reply To Address of Client
     * @param responseKey
     *            - {@link String} - Response Key of Consumer - expected to have placeHolder that needs to be replaced
     * @param responseMessage
     *            - {@link HasMessageProperties} - Response Message to be sent to Client
     * @param placeHolder
     *            - {@link String} - placeHolder String that needs to be replaced with replyToAddress
     * @throws IllegalArgumentException
     *             - Throws {@link IllegalArgumentException} if <b>responseKey</b> or <b>replyToAddress</b> or <b>placeHolder</b> is null
     */
    void sendMessage(String exchange, String replyToAddress, String responseKey,
            HasMessageProperties<? extends MessagePropertiesContainer> responseMessage, String placeHolder) throws IllegalArgumentException;

    /**
     * Send the message to the message bus. Here Consumer needs to pass a valid/unique Response Key. This Key should match with the response
     * Key used during registration of Capability. If the key contains any "ReplyTo" placeHolder that needs to be replaced, consumer should
     * do this before calling this method.
     * 
     * @param exchange
     *            - {@link String} - Response Exchange of Consumer
     * @param responseKey
     *            - {@link String} - Response Key of Consumer
     * @param responseMessage
     *            - {@link HasMessageProperties} - Response Message to be sent to Client
     */
    void sendMessage(String exchange, String responseKey, HasMessageProperties<? extends MessagePropertiesContainer> responseMessage);

}
