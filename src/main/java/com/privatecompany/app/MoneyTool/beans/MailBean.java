package com.privatecompany.app.MoneyTool.beans;

import com.privatecompany.app.MoneyTool.service.MailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailBean {

    @Bean
    public MailService getMailService(){
        return new MailService("cryptoshaairdrop@gmail.com", "parimatch1997");
    }
}
