package com.recargapay.walletservice.producer;

import com.recargapay.walletservice.configuration.RabbitMQConfig;
import com.recargapay.walletservice.dto.TransactionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionProducer {

  private final RabbitTemplate rabbitTemplate;

  public void send(TransactionMessage transactionMessage) {
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.TRANSACTION_EXCHANGE,
        RabbitMQConfig.TRANSACTION_ROUTING_KEY,
        transactionMessage
    );
  }

}
