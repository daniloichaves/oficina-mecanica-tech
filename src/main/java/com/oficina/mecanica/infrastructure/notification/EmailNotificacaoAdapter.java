package com.oficina.mecanica.infrastructure.notification;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.repositories.NotificacaoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificacaoAdapter implements NotificacaoPort {

    private final JavaMailSender mailSender;

    @Override
    public void notificarMudancaStatus(OrdemServico os) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("oficina@oficinamecanica.com");
            msg.setTo(os.getCliente().getEmail());
            msg.setSubject("OS #" + os.getId() + " - " + os.getStatus().getDescricao());
            msg.setText("Olá, " + os.getCliente().getNome()
                + "! Sua ordem de serviço #" + os.getId()
                + " mudou para o status: " + os.getStatus().getDescricao() + ".");
            mailSender.send(msg);
        } catch (Exception e) {
            // ponytail: best-effort — falha de SMTP não pode abortar a transição de status
            log.warn("Falha ao enviar e-mail de notificação da OS {}: {}", os.getId(), e.getMessage());
        }
    }
}
