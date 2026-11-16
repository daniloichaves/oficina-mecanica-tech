package com.oficina.billingservice.presentation.rest;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.dto.MercadoPagoWebhookRequest;
import com.oficina.billingservice.application.service.OrcamentoService;
import com.oficina.billingservice.domain.entity.Orcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Orçamentos e Pagamentos", description = "Gestão de orçamentos e integração com Mercado Pago")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/orcamentos")
    @Operation(summary = "Gerar orçamento para uma ordem de serviço")
    public ResponseEntity<Orcamento> criar(@Valid @RequestBody CriarOrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoService.criar(request));
    }

    @GetMapping("/orcamentos")
    @Operation(summary = "Listar todos os orçamentos")
    public ResponseEntity<List<Orcamento>> listarTodos() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/orcamentos/{id}")
    @Operation(summary = "Buscar orçamento por ID")
    public ResponseEntity<Orcamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @GetMapping("/orcamentos/ordem/{ordemId}")
    @Operation(summary = "Buscar orçamentos por ID da OS")
    public ResponseEntity<List<Orcamento>> buscarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(orcamentoService.buscarPorOrdemId(ordemId));
    }

    @PatchMapping("/orcamentos/{id}/aprovar")
    @Operation(summary = "Aprovar orçamento")
    public ResponseEntity<Orcamento> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.aprovar(id));
    }

    @PatchMapping("/orcamentos/{id}/rejeitar")
    @Operation(summary = "Rejeitar orçamento")
    public ResponseEntity<Orcamento> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.rejeitar(id));
    }

    @PatchMapping("/orcamentos/{id}/pagar")
    @Operation(summary = "Processar pagamento do orçamento via Mercado Pago")
    public ResponseEntity<Orcamento> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.pagar(id));
    }

    @PatchMapping("/orcamentos/{id}/compensar")
    @Operation(summary = "Compensar/estornar orçamento em falha da Saga")
    public ResponseEntity<Orcamento> compensar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.compensar(id));
    }

    @PostMapping("/orcamentos/webhook/mercadopago")
    @Operation(summary = "Webhook de confirmação de pagamento do Mercado Pago")
    public ResponseEntity<Orcamento> webhookMercadoPago(@Valid @RequestBody MercadoPagoWebhookRequest webhook) {
        return ResponseEntity.ok(orcamentoService.processarWebhookMercadoPago(webhook));
    }
}
