package com.recargapay.walletservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerUiWebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {

    registry.addRedirectViewController("/", "/swagger-ui/index.html");

    registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
  }

}
