package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.dto.VeiculoDTO;
import com.oficina.mecanica.application.services.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
public class VeiculoController {
    
    private final VeiculoService veiculoService;
    
    @PostMapping
    @Operation(summary = "Criar novo veículo")
    public ResponseEntity<VeiculoDTO> criar(@Valid @RequestBody VeiculoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    public ResponseEntity<VeiculoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }
    
    @GetMapping("/placa/{placa}")
    @Operation(summary = "Buscar veículo por placa")
    public ResponseEntity<VeiculoDTO> buscarPorPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(veiculoService.buscarPorPlaca(placa));
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os veículos")
    public ResponseEntity<List<VeiculoDTO>> listarTodos() {
        return ResponseEntity.ok(veiculoService.listarTodos());
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar veículos por cliente")
    public ResponseEntity<List<VeiculoDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(veiculoService.listarPorCliente(clienteId));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo")
    public ResponseEntity<VeiculoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoDTO dto) {
        return ResponseEntity.ok(veiculoService.atualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar veículo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
