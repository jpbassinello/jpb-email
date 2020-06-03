package br.com.jpb.email.service;

import br.com.jpb.email.exception.EmailProviderException;
import br.com.jpb.email.model.entity.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("jpb.email.active")
public class SendEmailJob {

	private final EmailService emailService;

	@Scheduled(fixedDelay = 30_000, initialDelay = 5_000)
	public void sendEmails() {
		for (Email email : emailService.findEmailsToSend()) {
			try {
				emailService.send(email);
			} catch (Exception e) {
				if (e instanceof EmailProviderException) {
					EmailProviderException epe = (EmailProviderException) e;
					log.warn(
							"Provider error SendEmailJob. Email Subject: {}. Email To: {}. Status Code: {}. Message: {}",
							email.getSubject(), email.getEmailTo(), epe.getStatusCode(), epe.getMessage(), e);
				} else {
					log.warn("Error in SendEmailJob. Email Subject: {}. Email To: {}",
							email.getSubject(), email.getEmailTo(), e);
				}
				emailService.updateTries(email);
			}
		}
	}

}
