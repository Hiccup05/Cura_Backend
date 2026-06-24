package com.hiccup.cura.enums;

import lombok.Getter;

@Getter
public enum PaymentProvider {
        KHALTI("khaltiPaymentStrategy"),
        ESEWA("esewaPaymentStrategy");

        private final String beanName;

        PaymentProvider(String beanName) {
            this.beanName = beanName;
        }

}
