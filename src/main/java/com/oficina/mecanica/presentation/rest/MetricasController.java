package com.oficina.mecanica.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
@Tag(name = "Métricas", description = "Métricas do sistema")
public class MetricasController {
    
    private final com.oficina.mecanica.application.services.OrdemServicoService ordemServicoService;
    
    @GetMapping("/tempo-medio-execucao")
    @Operation(summary = "Obter tempo médio de execução dos serviços")
    public ResponseEntity<Map<String, Double>> getTempoMedioExecucao() {
        Double tempoMedio = ordemServicoService.getTempoMedioExecucao();
        return ResponseEntity.ok(Map.of("tempoMedioMinutos", tempoMedio != null ? tempoMedio : 0.0));
    }
}
