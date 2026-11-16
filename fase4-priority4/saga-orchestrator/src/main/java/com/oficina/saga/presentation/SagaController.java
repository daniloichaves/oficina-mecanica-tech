package com.oficina.saga.presentation;

import com.oficina.saga.application.SagaEntity;
import com.oficina.saga.application.SagaOrchestratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sagas")
@Tag(name = "Saga Orchestrator", description = "Orquestração distribuída e compensação de Ordens de Serviço")
public class SagaController {

    private final SagaOrchestratorService service;

    public SagaController(SagaOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/ordens/{ordemId}/iniciar")
    @Operation(summary = "Iniciar orquestração da Saga para uma Ordem de Serviço")
    public ResponseEntity<SagaEntity> iniciar(@PathVariable Long ordemId) {
        return ResponseEntity.ok(service.iniciarNovaOrdem(ordemId));
    }

    @PostMapping("/ordens/{ordemId}/eventos")
    @Operation(summary = "Avançar ou compensar etapa da Saga a partir de um evento")
    public ResponseEntity<SagaEntity> evento(@PathVariable Long ordemId,
                                            @RequestParam String evento) {
        return ResponseEntity.ok(service.avancarEtapa(ordemId, evento));
    }

    @GetMapping("/ordens/{ordemId}")
    @Operation(summary = "Consultar estado e histórico da Saga por ID da OS")
    public ResponseEntity<SagaEntity> buscarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(service.buscarPorOrdemId(ordemId));
    }

    @GetMapping
    @Operation(summary = "Listar todas as sagas registradas")
    public ResponseEntity<List<SagaEntity>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }
}
