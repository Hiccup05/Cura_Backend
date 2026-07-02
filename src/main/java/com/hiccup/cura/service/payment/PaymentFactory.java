package com.hiccup.cura.service.payment;

import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.exception.custom.PaymentProviderNotSupported;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private final Map<String, PaymentStrategy> paymentStrategyMap;

    public PaymentStrategy createPaymentStrategy(PaymentProvider paymentProvider) {
        return Optional.ofNullable(paymentStrategyMap.get(paymentProvider.getBeanName()))
                .orElseThrow(() -> new PaymentProviderNotSupported(
                        paymentProvider + " is not supported in Cura."
                ));
    }
}
