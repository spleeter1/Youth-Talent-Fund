package com.graduation.youthtalentfund.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PayOsConfig {
    @Value("${payos.api-key}")
    private String apiKey;
    @Value("${payos.client-id}")
    private String clientId;
    @Value("${payos.checksum-key}")
    private String checksumKey;
}
