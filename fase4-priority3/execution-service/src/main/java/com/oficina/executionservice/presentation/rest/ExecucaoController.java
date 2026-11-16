package com.oficina.executionservice.presentation.rest;

import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.application.service.ExecucaoService;
import com.oficina.executionservice.domain.entity.Execucao;
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
@Tag(name = "Execução e Produção", description = "Gerenciamento da fila de execução em MongoDB")
public class ExecucaoController {

    private final ExecucaoService execucaoService;

    @PostMapping("/execucoes")
    @Operation(summary = "Criar item na fila de execução")
    public ResponseEntity<Execucao> criar(@Valid @RequestBody CriarExecucaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(execucaoService.criar(request));
    }

    @GetMapping("/execucoes")
    @Operation(summary = "Listar todas as execuções")
    public ResponseEntity<List<Execucao>> listarTodos() {
        return ResponseEntity.ok(execucaoService.listarTodos());
    }

    @GetMapping("/execucoes/{id}")
    @Operation(summary = "Buscar execução por ID")
    public ResponseEntity<Execucao> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(execucaoService.buscarPorId(id));
    }

    @GetMapping("/execucoes/ordem/{ordemId}")
    @Operation(summary = "Buscar execuções por ID da OS")
    public ResponseEntity<List<Execucao>> buscarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(execucaoService.buscarPorOrdemId(ordemId));
    }

    @PatchMapping("/execucoes/{id}/iniciar")
    @Operation(summary = "Iniciar execução")
    public ResponseEntity<Execucao> iniciar(@PathVariable String id) {
        return ResponseEntity.ok(execucaoService.iniciar(id));
    }

    @PatchMapping("/execucoes/{id}/status")
    @Operation(summary = "Atualizar status da execução")
    public ResponseEntity<Execucao> atualizarStatus(@PathVariable String id, @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(execucaoService.atualizarStatus(id, request));
    }

    @PatchMapping("/execucoes/{id}/finalizar")
    @Operation(summary = "Finalizar execução")
    public ResponseEntity<Execucao> finalizar(@PathVariable String id) {
        return ResponseEntity.ok(execucaoService.finalizar(id));
    }

    @PatchMapping("/execucoes/{id}/entregar")
    @Operation(summary = "Registrar entrega do veículo")
    public ResponseEntity<Execucao> entregar(@PathVariable String id) {
        return ResponseEntity.ok(execucaoService.entregar(id));
    }

    @PatchMapping("/execucoes/{id}/cancelar")
    @Operation(summary = "Cancelar execução (compensação da Saga)")
    public ResponseEntity<Execucao> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(execucaoService.cancelar(id));
    }
}
