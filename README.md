[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![Build Status](https://travis-ci.org/dellemc-symphony/common-messaging-parent.svg?branch=master)](https://travis-ci.org/dellemc-symphony/common-messaging-parent)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/75f9e97ba3a746d9bb0d0a9804aaed92)](https://www.codacy.com/app/chamap1/common-messaging-parent?utm_source=github.com&utm_medium=referral&utm_content=dellemc-symphony/common-messaging-parent&utm_campaign=badger)
[![Slack](http://slack.codecommunity.com/badge.svg)](https://codecommunity.slack.com/messages/symphony)
[![Codecov](https://img.shields.io/codecov/c/github/dellemc-symphony/common-messaging-parent.svg)](https://codecov.io/gh/dellemc-symphony/common-messaging-parent)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dell.cpsd/common-messaging-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dell.cpsd/common-messaging-parent)
[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)

# common-messaging-parent

## Description

This repository contains the source code for common-contract-enricher, common-testing, and common-rabbitmq.

* common-contract-enricher helps with the generation of classes from the API Java Shared Data (JSD) files.

* common-testing is a tiny artifact used as a test dependency to provide small utilities for JUnit testing purposes (such as message file reader function)
The testing dependency import should use the scope import:
```bash
<scope>import</scope>
```

* common-rabbitmq is a library of AMQP-related functions including but not limited to:
  * message aggregator
  * base Spring configuration
  * basic messaging validation checks
  * default retry policy
  * basic error handling

## Documentation
You can find additional documentation for Project Symphony at [dellemc-symphony.readthedocs.io](https://dellemc-symphony.readthedocs.io).

## Before you begin
Verify that the following tools are installed:

* Apache Maven 3.0.5+
* Java Development Kit (version 8)

## Building
Run the following command to build this project:
```bash
mvn clean install
```
## Useage

### Connecting to Rabbit MQ with SSL

For connecting to Rabbit MQ using SSL the following presequisites are required:-

* Get the root ca, client certificate and the key from tls-service. (Which inturn gets it form Vault) [README (tls-service)](https://github.com/dellemc-symphony/tls-service-parent/blob/refactor-wip/README.md)
    * Use the tls-service rest api to get the ca and the client certs.
* Get the amqp username and password from credential-service. (Which inturn gets it form Vault)
* Create a Java Key Store i.e. jks and import the keys and certs, using the following steps:-
    * Convert the client pem to der file 
    ```bash
    openssl x509 -outform der -in <client-cert>.pem -out <client-cert>.der
    ```
    * Export pem client key to pkcs12 format
    ```bash
    openssl pkcs12 -export -inkey <client-cert>.pem -in <client-cert>.pem -chain -CAfile <root-ca>.crt -out <pkcs12-file>.p12 -password pass:<pkcs12-password>
    ```
    * Create JKS from pkcs12
    ```bash
    keytool -importkeystore -deststorepass <jks-password> -destkeypass <jks-password> -destkeystore <jks-file>.jks -srckeystore <pkcs12-file>.p12 -srcstoretype PKCS12 -srcstorepass <pkcs12-password>
    ```
    * Import root ca to jks
    ```bash
    keytool -import -keystore <jks-file>.jks -file <root-ca>.crt -alias cpsd.root.ca -storepass <jks-password> -noprompt
    ```
    * Import client cert to jks
    ```bash
    keytool -import -keystore <jks-file>.jks -file <client-cert>.der -alias cpsd.intermideate.ca -storepass <jks-password> -noprompt
    ```
* Pass the jks ,its password and amqp credentials as JVM arguments.
    ```bash
    java -Xms64m -Xmx192m  \
        -cp <your-classpath-1>:<your-classpath-2> \
        -Dcontainer.id=$CONTAINERID \
        -Djavax.net.ssl.trustStore=<jks-file>.jks \
        -Djavax.net.ssl.trustStorePassword=<jks-password> \
        -Dlog4j.configuration=file:<log4j xml file> \
        -DactiveProfile=<profile> <your-main-class> \
        --remote.dell.amqp.rabbitUsername=<amqp-username> \
        --remote.dell.amqp.rabbitPassword=<amqp-password>
    ```
* The place where you create your RabbitMQCachingConnectionFactory bean use RabbitMQTLSFactoryBean from this project to configure SSL using your jks.
    ```java
    @Bean
    @Qualifier("rabbitConnectionFactory")
        public ConnectionFactory productionCachingConnectionFactory()
        {
            RabbitMQCachingConnectionFactory cachingCF = null;
            com.rabbitmq.client.ConnectionFactory connectionFactory;
            try{
                if (propertiesConfig.isSslEnabled())
                {
                    RabbitMQTLSFactoryBean rabbitMQTLSFactoryBean = new RabbitMQTLSFactoryBean(propertiesConfig);
                    connectionFactory = rabbitMQTLSFactoryBean.getObject();
                }
                else
                {
                    connectionFactory = new com.rabbitmq.client.ConnectionFactory();
                }
                cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return cachingCF;
        }
    ```
    
**NOTE :** If you want to configure your own SSL connection and don't want to use this project's SSL configuration with specific algorithm use amqp-rabbit project's RabbitConnectionFactoryBean.
For example:
    
```java
    @Bean
    @Qualifier("rabbitConnectionFactory")
        public ConnectionFactory productionCachingConnectionFactory()
        {
            RabbitMQCachingConnectionFactory cachingCF = null;
            com.rabbitmq.client.ConnectionFactory connectionFactory;
            try{
                if (propertiesConfig.isSslEnabled())
                {
                    RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
                    rabbitConnectionFactoryBean.setUseSSL(<true or false>);
                    rabbitConnectionFactoryBean.setSslAlgorithm(<ssl-version>);
                    rabbitConnectionFactoryBean.setKeyStoreType(<key-store-type jks or pkcs>);
                    rabbitConnectionFactoryBean.setTrustStoreType(<trust-store-type jks or pkcs>);
                    rabbitConnectionFactoryBean.setTrustStoreResource(new FileSystemResource(<path-to-trust-store>));
                    rabbitConnectionFactoryBean.setTrustStorePassphrase(<trust-store-password>);
                    rabbitConnectionFactoryBean.setKeyStoreResource(new FileSystemResource(<path-to-key-store>));
                    rabbitConnectionFactoryBean.setKeyStorePassphrase(<key-store-password>);
                    rabbitConnectionFactoryBean.afterPropertiesSet();
                    
                    //Override createSSLContext() to create and/or perform further modification of the context.
                    //Override setUpSSL() to take complete control over setting up SSL.
                    
                    connectionFactory = rabbitConnectionFactoryBean.getObject();
                }
                else
                {
                    connectionFactory = new com.rabbitmq.client.ConnectionFactory();
                }
                cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return cachingCF;
        }
``` 

### Connecting to Rabbit MQ without SSL
* Get amqp username and password from credential-service
* Pass amqp credentials as JVM arguments.
    ```bash
    java -Xms64m -Xmx192m  \
        -cp <your-classpath-1>:<your-classpath-2> \
        -Dcontainer.id=$CONTAINERID \
        -Dlog4j.configuration=file:<log4j xml file> \
        -DactiveProfile=<profile> <your-main-class> \
        --remote.dell.amqp.rabbitUsername=<amqp-username> \
        --remote.dell.amqp.rabbitPassword=<amqp-password>
    ```

## Contributing
Project Symphony is a collection of services and libraries housed at [GitHub][github].

Contribute code and make submissions at the relevant GitHub repository level. See [our documentation][contributing] for details on how to contribute.

## Community
Reach out to us on the Slack [#symphony][slack] channel by requesting an invite at [{code}Community][codecommunity].

You can also join [Google Groups][googlegroups] and start a discussion.
 
[slack]: https://codecommunity.slack.com/messages/symphony
[googlegroups]: https://groups.google.com/forum/#!forum/dellemc-symphony
[codecommunity]: http://community.codedellemc.com/
[contributing]: http://dellemc-symphony.readthedocs.io/en/latest/contributingtosymphony.html
[github]: https://github.com/dellemc-symphony
[documentation]: https://dellemc-symphony.readthedocs.io/en/latest/
