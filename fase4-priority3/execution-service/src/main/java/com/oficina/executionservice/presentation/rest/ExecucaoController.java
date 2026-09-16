package com.oficina.executionservice.presentation.rest;

import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.application.service.ExecucaoService;
import com.oficina.executionservice.domain.entity.Execucao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExecucaoController {

    private final ExecucaoService execucaoService;

    @PostMapping("/execucoes")
    public ResponseEntity<Execucao> criar(@Valid @RequestBody CriarExecucaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(execucaoService.criar(request));
    }

    @GetMapping("/execucoes")
    public ResponseEntity<List<Execucao>> listarTodos() {
        return ResponseEntity.ok(execucaoService.listarTodos());
    }

    @GetMapping("/execucoes/{id}")
    public ResponseEntity<Execucao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(execucaoService.buscarPorId(id));
    }

    @GetMapping("/execucoes/ordem/{ordemId}")
    public ResponseEntity<List<Execucao>> buscarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(execucaoService.buscarPorOrdemId(ordemId));
    }

    @PatchMapping("/execucoes/{id}/iniciar")
    public ResponseEntity<Execucao> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(execucaoService.iniciar(id));
    }

    @PatchMapping("/execucoes/{id}/status")
    public ResponseEntity<Execucao> atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(execucaoService.atualizarStatus(id, request));
    }

    @PatchMapping("/execucoes/{id}/finalizar")
    public ResponseEntity<Execucao> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(execucaoService.finalizar(id));
    }

    @PatchMapping("/execucoes/{id}/entregar")
    public ResponseEntity<Execucao> entregar(@PathVariable Long id) {
        return ResponseEntity.ok(execucaoService.entregar(id));
    }
}
