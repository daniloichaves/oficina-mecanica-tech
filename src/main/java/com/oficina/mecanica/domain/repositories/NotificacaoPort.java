package com.oficina.mecanica.domain.repositories;

import com.oficina.mecanica.domain.entities.OrdemServico;

/**
 * Port hexagonal de notificação: avisa o cliente sobre mudanças de status da OS.
 * Implementado por adapter de e-mail na infraestrutura.
 */
public interface NotificacaoPort {
    void notificarMudancaStatus(OrdemServico ordemServico);
}
