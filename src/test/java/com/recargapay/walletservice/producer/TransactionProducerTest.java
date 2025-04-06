package com.recargapay.walletservice.producer;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.recargapay.walletservice.configuration.RabbitMQConfig;
import com.recargapay.walletservice.dto.TransactionMessage;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class TransactionProducerTest {

  private RabbitTemplate rabbitTemplate;
  private TransactionProducer transactionProducer;

  @BeforeEach
  void setUp() {
    rabbitTemplate = mock(RabbitTemplate.class);
    transactionProducer = new TransactionProducer(rabbitTemplate);
  }

  @Test
  void shouldSendTransactionMessage() {
    TransactionMessage transactionMessage = TransactionMessage.builder()
        .transactionCode(UUID.randomUUID())
        .build();

    transactionProducer.send(transactionMessage);

    verify(rabbitTemplate, times(1)).convertAndSend(
        eq(RabbitMQConfig.TRANSACTION_EXCHANGE),
        eq(RabbitMQConfig.TRANSACTION_ROUTING_KEY),
        eq(transactionMessage)
    );
  }
}
