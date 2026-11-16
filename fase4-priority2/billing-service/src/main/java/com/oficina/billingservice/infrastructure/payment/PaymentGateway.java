package com.oficina.billingservice.infrastructure.payment;

import com.oficina.billingservice.domain.entity.Orcamento;

public interface PaymentGateway {
    PaymentResult processarPagamento(Orcamento orcamento);
    void estornarPagamento(String paymentId);

    record PaymentResult(String paymentId, String status, String provider) {}
}
