package com.oficina.osservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarOrdemServicoRequest(
        @NotBlank(message = "Placa do veículo é obrigatória") String placaVeiculo,
        @NotBlank(message = "Cliente é obrigatório") String clienteId,
        @NotBlank(message = "Nome do cliente é obrigatório") String nomeCliente,
        @NotBlank(message = "Descrição do defeito é obrigatória") String descricaoDefeito
) {
}
