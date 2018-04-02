package com.db.awmd.challenge;

import com.db.awmd.challenge.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class Config {
  @Primary
  @Bean
  public NotificationService notificationService() {
    return (account, transferDescription) -> System.out.println("Simulating notification to account["+ account.getAccountId() +"]: " + transferDescription);
  }
}
