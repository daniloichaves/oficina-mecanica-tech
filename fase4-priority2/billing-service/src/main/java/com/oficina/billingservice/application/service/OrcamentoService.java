package com.oficina.billingservice.application.service;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.dto.MercadoPagoWebhookRequest;
import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.infrastructure.event.BillingEventPublisher;
import com.oficina.billingservice.infrastructure.payment.PaymentGateway;
import com.oficina.billingservice.infrastructure.persistence.OrcamentoRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final PaymentGateway paymentGateway;
    private final BillingEventPublisher eventPublisher;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    public Orcamento criar(CriarOrcamentoRequest request) {
        Orcamento orcamento = Orcamento.builder()
                .ordemId(request.ordemId())
                .clienteId(request.clienteId())
                .valor(request.valor())
                .status(StatusOrcamento.PENDENTE)
                .build();

        Orcamento salvo = repository.save(orcamento);
        incrementCounter("billing.orcamentos.criados");
        eventPublisher.publish("orcamento.criado", salvo);
        return salvo;
    }

    @Transactional(readOnly = true)
    public Orcamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Orcamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Orcamento> buscarPorOrdemId(Long ordemId) {
        return repository.findByOrdemId(ordemId);
    }

    public Orcamento aprovar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.aprovar();
        Orcamento salvo = repository.save(orcamento);
        incrementCounter("billing.orcamentos.aprovados");
        eventPublisher.publish("orcamento.aprovado", salvo);
        return salvo;
    }

    public Orcamento rejeitar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.rejeitar();
        Orcamento salvo = repository.save(orcamento);
        incrementCounter("billing.orcamentos.rejeitados");
        eventPublisher.publish("orcamento.rejeitado", salvo);
        return salvo;
    }

    public Orcamento pagar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        if (orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new IllegalStateException("Só é possível pagar um orçamento aprovado");
        }
        PaymentGateway.PaymentResult payment = paymentGateway.processarPagamento(orcamento);
        orcamento.setPaymentId(payment.paymentId());
        orcamento.setPaymentStatus(payment.status());
        orcamento.setPaymentProvider(payment.provider());
        orcamento.pagar();
        Orcamento salvo = repository.save(orcamento);
        incrementCounter("billing.orcamentos.pagos");
        eventPublisher.publish("orcamento.pago", salvo);
        return salvo;
    }

    public Orcamento processarWebhookMercadoPago(MercadoPagoWebhookRequest webhook) {
        Orcamento orcamento = buscarPorId(webhook.orcamentoId());
        orcamento.setPaymentId(webhook.paymentId());
        orcamento.setPaymentStatus(webhook.status());
        orcamento.setPaymentProvider("MERCADO_PAGO");
        if ("approved".equalsIgnoreCase(webhook.status())) {
            if (orcamento.getStatus() == StatusOrcamento.PENDENTE) {
                orcamento.aprovar();
            }
            if (orcamento.getStatus() == StatusOrcamento.APROVADO) {
                orcamento.pagar();
                eventPublisher.publish("orcamento.pago", orcamento);
            }
        } else if ("rejected".equalsIgnoreCase(webhook.status()) && orcamento.getStatus() == StatusOrcamento.PENDENTE) {
            orcamento.rejeitar();
            eventPublisher.publish("orcamento.rejeitado", orcamento);
        }
        return repository.save(orcamento);
    }

    public Orcamento compensar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        if (orcamento.getStatus() == StatusOrcamento.PAGO && orcamento.getPaymentId() != null) {
            paymentGateway.estornarPagamento(orcamento.getPaymentId());
        }
        orcamento.compensar();
        Orcamento salvo = repository.save(orcamento);
        incrementCounter("billing.orcamentos.compensados");
        eventPublisher.publish("orcamento.compensado", salvo);
        return salvo;
    }

    private void incrementCounter(String name) {
        if (meterRegistry != null) {
            Counter.builder(name).register(meterRegistry).increment();
        }
    }
}
