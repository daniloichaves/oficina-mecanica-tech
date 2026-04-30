package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {
    
    @Test
    void deveIniciarComStatusRecebida() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();
        
        assertEquals(StatusOrdemServico.RECEBIDA, os.getStatus());
    }
    
    @Test
    void deveIniciarDiagnostico() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();
        
        os.iniciarDiagnostico();
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, os.getStatus());
    }
    
    @Test
    void naoDeveIniciarDiagnosticoSeNaoRecebida() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .build();
        
        assertThrows(IllegalStateException.class, os::iniciarDiagnostico);
    }
    
    @Test
    void deveConcluirDiagnostico() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .build();
        
        os.concluirDiagnostico();
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, os.getStatus());
    }
    
    @Test
    void deveAprovarOrcamento() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.AGUARDANDO_APROVACAO)
            .orcamentoAprovado(false)
            .build();
        
        os.aprovarOrcamento();
        assertEquals(StatusOrdemServico.EM_EXECUCAO, os.getStatus());
        assertTrue(os.getOrcamentoAprovado());
    }
    
    @Test
    void deveFinalizar() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_EXECUCAO)
            .build();
        
        os.finalizar();
        assertEquals(StatusOrdemServico.FINALIZADA, os.getStatus());
    }
    
    @Test
    void deveEntregar() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.FINALIZADA)
            .build();
        
        os.entregar();
        assertEquals(StatusOrdemServico.ENTREGUE, os.getStatus());
        assertNotNull(os.getDataEntrega());
    }
}
