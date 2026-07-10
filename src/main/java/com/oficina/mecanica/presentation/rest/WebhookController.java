package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.dto.NotificacaoOrcamentoDTO;
import com.oficina.mecanica.application.dto.OrdemServicoDTO;
import com.oficina.mecanica.application.services.OrdemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Notificações externas (aprovação/recusa de orçamento)")
public class WebhookController {

    private final OrdemServicoService ordemServicoService;

    @PostMapping("/orcamento")
    @Operation(summary = "Receber notificação externa de aprovação ou recusa do orçamento")
    public ResponseEntity<OrdemServicoDTO> notificarOrcamento(@Valid @RequestBody NotificacaoOrcamentoDTO dto) {
        OrdemServicoDTO os = Boolean.TRUE.equals(dto.getAprovado())
            ? ordemServicoService.aprovarOrcamento(dto.getOrdemServicoId())
            : ordemServicoService.recusarOrcamento(dto.getOrdemServicoId());
        return ResponseEntity.ok(os);
    }
}
