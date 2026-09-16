package com.oficina.saga.presentation;

import com.oficina.saga.application.SagaEntity;
import com.oficina.saga.application.SagaOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final SagaOrchestratorService service;

    public SagaController(SagaOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/ordens/{ordemId}/iniciar")
    public ResponseEntity<SagaEntity> iniciar(@PathVariable Long ordemId) {
        return ResponseEntity.ok(service.iniciarNovaOrdem(ordemId));
    }

    @PostMapping("/ordens/{ordemId}/eventos")
    public ResponseEntity<SagaEntity> evento(@PathVariable Long ordemId,
                                            @RequestParam String evento) {
        return ResponseEntity.ok(service.avancarEtapa(ordemId, evento));
    }
}
