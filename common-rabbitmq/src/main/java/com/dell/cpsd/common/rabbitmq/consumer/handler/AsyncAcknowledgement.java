/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.consumer.handler;

import java.util.concurrent.CompletableFuture;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 * @param <T>
 */
public interface AsyncAcknowledgement<T>
{
    /**
     * Register Async request using the correlation ID
     * 
     * @param correlationId
     *            - {@link String}
     * @return {@link CompletableFuture<T>}
     */
    CompletableFuture<T> register(String correlationId);
}
