package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.dto.ServicoDTO;
import com.oficina.mecanica.application.services.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
public class ServicoController {
    
    private final ServicoService servicoService;
    
    @PostMapping
    @Operation(summary = "Criar novo serviço")
    public ResponseEntity<ServicoDTO> criar(@Valid @RequestBody ServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    public ResponseEntity<ServicoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os serviços")
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
    public ResponseEntity<ServicoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ServicoDTO dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
