package com.oficina.billingservice.infrastructure.payment;

import com.oficina.billingservice.domain.entity.Orcamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MercadoPagoPaymentGateway implements PaymentGateway {

    private final RestClient restClient;
    private final String accessToken;

    public MercadoPagoPaymentGateway(
            @Value("${app.mercadopago.base-url:https://api.mercadopago.com}") String baseUrl,
            @Value("${app.mercadopago.access-token:}") String accessToken) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.accessToken = accessToken;
    }

    @Override
    public PaymentResult processarPagamento(Orcamento orcamento) {
        if (accessToken == null || accessToken.isBlank()) {
            String sandboxId = "mp-sandbox-" + orcamento.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
            log.info("Mercado Pago sandbox (sem token configurado): pagamento {} aprovado para orçamento {}",
                    sandboxId, orcamento.getId());
            return new PaymentResult(sandboxId, "approved", "MERCADO_PAGO_SANDBOX");
        }

        Map<?, ?> response = restClient.post()
                .uri("/v1/payments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "transaction_amount", orcamento.getValor(),
                        "description", "Oficina Mecânica - OS #" + orcamento.getOrdemId(),
                        "external_reference", String.valueOf(orcamento.getOrdemId()),
                        "payer", Map.of("email", orcamento.getClienteId() + "@cliente.oficina.local")
                ))
                .retrieve()
                .body(Map.class);

        String paymentId = response != null && response.get("id") != null
                ? String.valueOf(response.get("id"))
                : "mp-" + UUID.randomUUID();
        String status = response != null && response.get("status") != null
                ? String.valueOf(response.get("status"))
                : "approved";
        return new PaymentResult(paymentId, status, "MERCADO_PAGO");
    }

    @Override
    public void estornarPagamento(String paymentId) {
        if (paymentId == null || paymentId.isBlank() || accessToken == null || accessToken.isBlank()) {
            log.info("Mercado Pago sandbox/local: estorno registrado para pagamento {}", paymentId);
            return;
        }
        restClient.post()
                .uri("/v1/payments/{id}/refunds", paymentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }
}
