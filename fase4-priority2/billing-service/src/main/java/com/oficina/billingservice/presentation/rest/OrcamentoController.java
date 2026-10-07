package com.oficina.billingservice.presentation.rest;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.service.OrcamentoService;
import com.oficina.billingservice.domain.entity.Orcamento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/orcamentos")
    public ResponseEntity<Orcamento> criar(@Valid @RequestBody CriarOrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoService.criar(request));
    }

    @GetMapping("/orcamentos")
    public ResponseEntity<List<Orcamento>> listarTodos() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/orcamentos/{id}")
    public ResponseEntity<Orcamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @GetMapping("/orcamentos/ordem/{ordemId}")
    public ResponseEntity<List<Orcamento>> buscarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(orcamentoService.buscarPorOrdemId(ordemId));
    }

    @PatchMapping("/orcamentos/{id}/aprovar")
    public ResponseEntity<Orcamento> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.aprovar(id));
    }

    @PatchMapping("/orcamentos/{id}/rejeitar")
    public ResponseEntity<Orcamento> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.rejeitar(id));
    }

    @PatchMapping("/orcamentos/{id}/pagar")
    public ResponseEntity<Orcamento> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.pagar(id));
    }
}
