package com.hiccup.cura.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhaltiResponseDto {
    private String pidx;
    @JsonProperty("payment_url")
    private String paymentUrl;
    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;
    @JsonProperty("expires_in")
    private int expiresIn;
}
