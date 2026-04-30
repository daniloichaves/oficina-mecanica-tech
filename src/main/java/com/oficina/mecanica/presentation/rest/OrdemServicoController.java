package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.dto.CriarOrdemServicoDTO;
import com.oficina.mecanica.application.dto.OrdemServicoDTO;
import com.oficina.mecanica.application.services.OrdemServicoService;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço")
public class OrdemServicoController {
    
    private final OrdemServicoService ordemServicoService;
    
    @PostMapping
    @Operation(summary = "Criar nova ordem de serviço")
    public ResponseEntity<OrdemServicoDTO> criar(@Valid @RequestBody CriarOrdemServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem de serviço por ID")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as ordens de serviço")
    public ResponseEntity<List<OrdemServicoDTO>> listarTodos() {
        return ResponseEntity.ok(ordemServicoService.listarTodos());
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar ordens de serviço por cliente")
    public ResponseEntity<List<OrdemServicoDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ordemServicoService.listarPorCliente(clienteId));
    }
    
    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Listar ordens de serviço por veículo")
    public ResponseEntity<List<OrdemServicoDTO>> listarPorVeiculo(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(ordemServicoService.listarPorVeiculo(veiculoId));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar ordens de serviço por status")
    public ResponseEntity<List<OrdemServicoDTO>> listarPorStatus(@PathVariable StatusOrdemServico status) {
        return ResponseEntity.ok(ordemServicoService.listarPorStatus(status));
    }
    
    @PatchMapping("/{id}/iniciar-diagnostico")
    @Operation(summary = "Iniciar diagnóstico da OS")
    public ResponseEntity<OrdemServicoDTO> iniciarDiagnostico(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.iniciarDiagnostico(id));
    }
    
    @PatchMapping("/{id}/concluir-diagnostico")
    @Operation(summary = "Concluir diagnóstico da OS")
    public ResponseEntity<OrdemServicoDTO> concluirDiagnostico(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.concluirDiagnostico(id));
    }
    
    @PatchMapping("/{id}/aprovar-orcamento")
    @Operation(summary = "Aprovar orçamento da OS")
    public ResponseEntity<OrdemServicoDTO> aprovarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.aprovarOrcamento(id));
    }
    
    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar OS")
    public ResponseEntity<OrdemServicoDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.finalizar(id));
    }
    
    @PatchMapping("/{id}/entregar")
    @Operation(summary = "Entregar veículo")
    public ResponseEntity<OrdemServicoDTO> entregar(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.entregar(id));
    }
}
