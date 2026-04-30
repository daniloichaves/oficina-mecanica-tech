package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.application.services.OrdemServicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricasControllerTest {

    @Mock
    private OrdemServicoService ordemServicoService;

    @InjectMocks
    private MetricasController metricasController;

    @Test
    void getTempoMedioExecucao_ComValor_DeveRetornarTempoMedio() {
        when(ordemServicoService.getTempoMedioExecucao()).thenReturn(45.5);

        ResponseEntity<Map<String, Double>> response = metricasController.getTempoMedioExecucao();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(45.5, response.getBody().get("tempoMedioMinutos"));
        verify(ordemServicoService).getTempoMedioExecucao();
    }

    @Test
    void getTempoMedioExecucao_ComNulo_DeveRetornarZero() {
        when(ordemServicoService.getTempoMedioExecucao()).thenReturn(null);

        ResponseEntity<Map<String, Double>> response = metricasController.getTempoMedioExecucao();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0.0, response.getBody().get("tempoMedioMinutos"));
        verify(ordemServicoService).getTempoMedioExecucao();
    }
}
