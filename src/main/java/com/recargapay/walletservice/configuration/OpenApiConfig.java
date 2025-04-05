package com.recargapay.walletservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI walletServiceOpenAPI() {
    return new OpenAPI()
        .components(new Components())
        .info(apiInfo())
        .addServersItem(new Server().url("/"));
  }

  private Info apiInfo() {
    return new Info()
        .title("Wallet Service API")
        .description("API for managing wallets: deposit, withdraw, transfer money")
        .version("v1.0.0");
  }

}
