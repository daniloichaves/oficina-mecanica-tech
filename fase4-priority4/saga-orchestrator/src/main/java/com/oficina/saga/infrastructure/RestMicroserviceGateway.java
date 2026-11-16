package com.oficina.saga.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.orchestration.remote-enabled", havingValue = "true")
public class RestMicroserviceGateway implements MicroserviceGateway {

    private final RestClient osClient;
    private final RestClient billingClient;
    private final RestClient executionClient;

    public RestMicroserviceGateway(
            @Value("${app.clients.os-service-url:http://localhost:8081}") String osUrl,
            @Value("${app.clients.billing-service-url:http://localhost:8082}") String billingUrl,
            @Value("${app.clients.execution-service-url:http://localhost:8083}") String executionUrl) {
        this.osClient = RestClient.builder().baseUrl(osUrl).build();
        this.billingClient = RestClient.builder().baseUrl(billingUrl).build();
        this.executionClient = RestClient.builder().baseUrl(executionUrl).build();
    }

    @Override
    public Long criarOrcamento(Long ordemId, String clienteId, BigDecimal valor) {
        Map<?, ?> body = billingClient.post()
                .uri("/api/orcamentos")
                .header(CorrelationIdFilter.HEADER_NAME, correlationId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("ordemId", ordemId, "clienteId", clienteId, "valor", valor))
                .retrieve()
                .body(Map.class);
        return body != null && body.get("id") != null ? ((Number) body.get("id")).longValue() : null;
    }

    @Override
    public void compensarOrcamento(Long orcamentoId) {
        if (orcamentoId == null) {
            return;
        }
        billingClient.patch()
                .uri("/api/orcamentos/{id}/compensar", orcamentoId)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId())
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public String criarExecucao(Long ordemId, String clienteId, String descricao) {
        Map<?, ?> body = executionClient.post()
                .uri("/api/execucoes")
                .header(CorrelationIdFilter.HEADER_NAME, correlationId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("ordemId", ordemId, "clienteId", clienteId, "descricaoTarefa", descricao))
                .retrieve()
                .body(Map.class);
        return body != null && body.get("id") != null ? String.valueOf(body.get("id")) : null;
    }

    @Override
    public void atualizarStatusOs(Long ordemId, String status) {
        osClient.patch()
                .uri("/api/os/{id}/status", ordemId)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", status))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void cancelarOs(Long ordemId) {
        osClient.patch()
                .uri("/api/os/{id}/cancelar", ordemId)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId())
                .retrieve()
                .toBodilessEntity();
    }

    private String correlationId() {
        String id = MDC.get(CorrelationIdFilter.MDC_KEY);
        return id != null ? id : "";
    }
}
