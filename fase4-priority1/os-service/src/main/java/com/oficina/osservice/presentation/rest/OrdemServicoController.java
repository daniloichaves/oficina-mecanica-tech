package com.oficina.osservice.presentation.rest;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
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
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço no microsserviço OS Service")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    @PostMapping("/os")
    @Operation(summary = "Abrir nova ordem de serviço")
    public ResponseEntity<OrdemServico> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criar(request));
    }

    @GetMapping("/os/{id}")
    @Operation(summary = "Buscar ordem de serviço por ID")
    public ResponseEntity<OrdemServico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping("/os")
    @Operation(summary = "Listar todas as ordens de serviço")
    public ResponseEntity<List<OrdemServico>> listarTodos() {
        return ResponseEntity.ok(ordemServicoService.listarTodos());
    }

    @GetMapping("/os/status/{status}")
    @Operation(summary = "Listar ordens de serviço por status")
    public ResponseEntity<List<OrdemServico>> buscarPorStatus(@PathVariable StatusOrdemServico status) {
        return ResponseEntity.ok(ordemServicoService.buscarPorStatus(status));
    }

    @PatchMapping("/os/{id}/status")
    @Operation(summary = "Atualizar status da ordem de serviço")
    public ResponseEntity<OrdemServico> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(ordemServicoService.atualizarStatus(id, request));
    }

    @PatchMapping("/os/{id}/cancelar")
    @Operation(summary = "Cancelar ordem de serviço (compensação da Saga)")
    public ResponseEntity<OrdemServico> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.cancelar(id));
    }
}
