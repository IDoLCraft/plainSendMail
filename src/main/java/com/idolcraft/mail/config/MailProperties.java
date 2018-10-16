package com.idolcraft.mail.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String from;
    private String bcc;
    private String subject;
    private String attacheName;
    private String genderParamMale;
    private String genderParamFemale;
}
