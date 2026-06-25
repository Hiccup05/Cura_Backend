package com.hiccup.cura.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class EsewaSignatureGenerator {
    @Value("${esewa.secret}")
    private String secret;
    public String getSignature(String message) throws Exception {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(),"HmacSHA256");
            sha256HMAC.init(secretKey);
            return Base64.encodeBase64String(sha256HMAC.doFinal(message.getBytes()));
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
