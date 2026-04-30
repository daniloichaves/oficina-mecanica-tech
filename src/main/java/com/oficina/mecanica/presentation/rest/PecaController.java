package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.dto.PecaDTO;
import com.oficina.mecanica.application.services.PecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
@Tag(name = "Peças", description = "Gerenciamento de peças e insumos")
public class PecaController {
    
    private final PecaService pecaService;
    
    @PostMapping
    @Operation(summary = "Criar nova peça")
    public ResponseEntity<PecaDTO> criar(@Valid @RequestBody PecaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar peça por ID")
    public ResponseEntity<PecaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pecaService.buscarPorId(id));
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as peças")
    public ResponseEntity<List<PecaDTO>> listarTodos() {
        return ResponseEntity.ok(pecaService.listarTodos());
    }
    
    @GetMapping("/estoque-baixo")
    @Operation(summary = "Listar peças com estoque baixo")
    public ResponseEntity<List<PecaDTO>> listarEstoqueBaixo(@RequestParam(defaultValue = "5") Integer limite) {
        return ResponseEntity.ok(pecaService.listarEstoqueBaixo(limite));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar peça")
    public ResponseEntity<PecaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PecaDTO dto) {
        return ResponseEntity.ok(pecaService.atualizar(id, dto));
    }
    
    @PatchMapping("/{id}/estoque")
    @Operation(summary = "Atualizar estoque da peça")
    public ResponseEntity<PecaDTO> atualizarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(pecaService.atualizarEstoque(id, quantidade));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar peça")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pecaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
