package com.oficina.osservice.presentation.rest;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.OrdemServico;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    @PostMapping("/os")
    public ResponseEntity<OrdemServico> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criar(request));
    }

    @GetMapping("/os/{id}")
    public ResponseEntity<OrdemServico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping("/os")
    public ResponseEntity<List<OrdemServico>> listarTodos() {
        return ResponseEntity.ok(ordemServicoService.listarTodos());
    }

    @PatchMapping("/os/{id}/status")
    public ResponseEntity<OrdemServico> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(ordemServicoService.atualizarStatus(id, request));
    }
}
