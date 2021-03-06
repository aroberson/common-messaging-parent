/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.common.rabbitmq.context;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.rabbitmq.log.RabbitMQLoggingManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since SINCE-TBD
 */
@Component
public class RabbitContextListener implements ApplicationListener<ApplicationContextEvent>
{
    private static final ILogger LOGGER = RabbitMQLoggingManager.getLogger(RabbitContextListener.class);

    @Override
    public void onApplicationEvent(ApplicationContextEvent event)
    {
        if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent || event instanceof ContextRefreshedEvent)
        {
            ApplicationContext context = event.getApplicationContext();

            RabbitContext rabbitContext = context.getBean(RabbitContext.class);
            if (rabbitContext == null)
            {
                return;
            }

            if (event instanceof ContextRefreshedEvent)
            {
                Map<String, RabbitContextAware> awares = context.getBeansOfType(RabbitContextAware.class);
                for (RabbitContextAware aware : awares.values())
                {
                    aware.setRabbitContext(rabbitContext);
                }
            }

            if (context instanceof ConfigurableApplicationContext)
            {
                ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
                injectAll(configurableApplicationContext, rabbitContext.getExchanges());
                injectAll(configurableApplicationContext, rabbitContext.getQueues());
                injectAll(configurableApplicationContext, rabbitContext.getBindings());
                injectAll(configurableApplicationContext, rabbitContext.getContainers());

                inject(configurableApplicationContext, rabbitContext.getRabbitTemplate());
                inject(configurableApplicationContext, rabbitContext.getAdmin());
                inject(configurableApplicationContext, rabbitContext.getMessageConverter());
                inject(configurableApplicationContext, rabbitContext.getMessageConverter().getClassMapper());
            }
        }
    }

    private void injectAll(ConfigurableApplicationContext context, Collection beans)
    {
        if (beans != null)
        {
            beans.forEach(bean -> inject(context, bean));
        }
    }

    private void inject(ConfigurableApplicationContext context, Object bean)
    {
        if (bean != null)
        {
            context.getBeanFactory().registerSingleton(singletonBeanName(bean), bean);

            if (bean instanceof InitializingBean)
            {
                InitializingBean initializingBean = (InitializingBean) bean;
                try
                {
                    initializingBean.afterPropertiesSet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            if (bean instanceof ApplicationContextAware)
            {
                ApplicationContextAware contextAware = (ApplicationContextAware) bean;
                try
                {
                    contextAware.setApplicationContext(context);
                }
                catch (BeansException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private String singletonBeanName(Object bean)
    {
        return bean.getClass().getSimpleName() + "@" + UUID.randomUUID().toString();
    }
}
