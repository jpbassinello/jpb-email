package br.com.jpb.service;

import br.com.jpb.model.entity.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
public class SendEmailJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailJob.class);

	@Inject
	private transient EmailService emailService;

	@Scheduled(fixedDelay = 20_000)
	public void sendEmails() {
		final List<Email> toSend = emailService.findEmailsToSend();
		LOGGER.info("Iniciando job para envio de emails. Quantidade de emails a enviar: " + toSend
				.size());
		for (Email email : toSend) {
			try {
				emailService.send(email);
			} catch (Exception e) {
				LOGGER.warn("Erro no job para envio de emails. Assunto do email: {}. " +
						"Destinatário:" + " {}", email.getSubject(), email.getEmailTo());
				emailService.updateTries(email);
			}
		}
		LOGGER.info("Finalizando job para envio de emails.");
	}

}
