package com.hiccup.cura.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class EsewaSignatureGenerator {

    @Value("${esewa.secret}")
    private String secret;

    private static final String HMAC_SHA256 = "HmacSHA256";

    public String getSignature(String message) {
        try {
            Mac sha256HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            sha256HMAC.init(secretKey);

            byte[] rawHmac = sha256HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(rawHmac);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Internal cryptographic configuration failure while signing message", e);
            throw new IllegalStateException("Failed to initialize cryptographic signature component", e);
        }
    }
}
