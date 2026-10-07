package com.oficina.billingservice;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.service.OrcamentoService;
import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.infrastructure.persistence.OrcamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OrcamentoService.class)
class OrcamentoServiceTest {

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Test
    void deveCriarOrcamentoEmStatusPendente() {
        CriarOrcamentoRequest request = new CriarOrcamentoRequest(99L, "CLIENTE-123", new BigDecimal("350.00"));

        Orcamento orcamento = orcamentoService.criar(request);

        assertThat(orcamento.getId()).isNotNull();
        assertThat(orcamento.getStatus()).isEqualTo(StatusOrcamento.PENDENTE);
        assertThat(orcamento.getValor()).isEqualByComparingTo("350.00");
        assertThat(orcamentoRepository.count()).isEqualTo(1);
    }

    @Test
    void deveAprovarOrcamento() {
        Orcamento orcamento = orcamentoService.criar(new CriarOrcamentoRequest(77L, "CLIENTE-77", new BigDecimal("150.50")));

        Orcamento atualizado = orcamentoService.aprovar(orcamento.getId());

        assertThat(atualizado.getStatus()).isEqualTo(StatusOrcamento.APROVADO);
    }
}
