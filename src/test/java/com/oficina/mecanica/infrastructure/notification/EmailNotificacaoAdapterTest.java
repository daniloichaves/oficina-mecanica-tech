package com.oficina.mecanica.infrastructure.notification;

import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificacaoAdapterTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks private EmailNotificacaoAdapter adapter;

    private OrdemServico os() {
        Cliente cliente = Cliente.builder().id(1L).nome("Ana").email("ana@ex.com").build();
        return OrdemServico.builder().id(7L).cliente(cliente)
            .status(StatusOrdemServico.EM_EXECUCAO).build();
    }

    @Test
    void deveEnviarEmailComDadosDaOS() {
        adapter.notificarMudancaStatus(os());

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("ana@ex.com", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("OS #7"));
        assertTrue(msg.getSubject().contains("Em Execução"));
        assertTrue(msg.getText().contains("Ana"));
        assertTrue(msg.getText().contains("Em Execução"));
    }

    @Test
    void naoDevePropagarFalhaDeSmtp() {
        doThrow(new MailSendException("smtp fora")).when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> adapter.notificarMudancaStatus(os()));
    }
}
