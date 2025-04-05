package com.recargapay.walletservice.consumer;

import com.recargapay.walletservice.configuration.RabbitMQConfig;
import com.recargapay.walletservice.dto.TransactionMessage;
import com.recargapay.walletservice.service.TransactionService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionConsumer {
  private final TransactionService transactionService;
  private final MeterRegistry meterRegistry;

  @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
  public void consume(TransactionMessage message) {
    log.info("m=consume, step=start, transactionCode={}", message.getTransactionCode());
    try {
      transactionService.process(message);
      meterRegistry.counter("transaction.messages.consumed.success").increment();
      log.info("m=consume, step=success, transactionCode={}", message.getTransactionCode());
    } catch (Exception e) {
      meterRegistry.counter("transaction.messages.consumed.failure").increment();
      log.error("Error processing message. Sending to DLQ. TransactionCode={}", message.getTransactionCode(), e);
      throw new RuntimeException("Failed to process message, moving to DLQ", e);
    }
  }

}
