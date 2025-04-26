package com.vaibhav.booking.system.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPropertiesConfig {
    private String secret;
}
