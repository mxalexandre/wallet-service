package com.recargapay.walletservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String TRANSACTION_QUEUE = "transaction-queue";
  public static final String DLQ_QUEUE = "transaction.dlq.queue";

  public static final String TRANSACTION_EXCHANGE = "transaction-exchange";
  public static final String DLQ_EXCHANGE = "transaction.dlq.exchange";

  public static final String TRANSACTION_ROUTING_KEY = "transaction-routing-key";
  public static final String DLQ_ROUTING_KEY = "transaction-dlq-routing-key";

  @Bean
  public Queue transactionQueue() {
    return QueueBuilder.durable(TRANSACTION_QUEUE)
        .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  public Queue dlqQueue() {
    return QueueBuilder.durable(DLQ_QUEUE).build();
  }

  @Bean
  public DirectExchange transactionExchange() {
    return new DirectExchange(TRANSACTION_EXCHANGE);
  }

  @Bean
  public DirectExchange dlqExchange() {
    return new DirectExchange(DLQ_EXCHANGE);
  }

  @Bean
  public Binding transactionBinding(Queue transactionQueue, DirectExchange transactionExchange) {
    return BindingBuilder.bind(transactionQueue)
        .to(transactionExchange)
        .with(TRANSACTION_ROUTING_KEY);
  }

  @Bean
  public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange) {
    return BindingBuilder.bind(dlqQueue)
        .to(dlqExchange)
        .with(DLQ_ROUTING_KEY);
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory, RabbitMQMessageConverter messageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setDefaultRequeueRejected(false);
    factory.setMessageConverter(messageConverter.jsonMessageConverter());
    factory.setAdviceChain(RetryInterceptorBuilder.stateless()
        .maxAttempts(3)
        .recoverer(new RejectAndDontRequeueRecoverer())
        .build()
    );
    return factory;
  }

}
