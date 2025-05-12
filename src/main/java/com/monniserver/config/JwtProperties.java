package com.monniserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtProperties {
    private String secret;
    private long expireLength;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireLength() {
        return expireLength;
    }

    public void setExpireLength(long expireLength) {
        this.expireLength = expireLength;
    }
}
