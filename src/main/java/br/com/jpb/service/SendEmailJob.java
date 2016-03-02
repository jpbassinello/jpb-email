package br.com.jpb.service;

import br.com.jpb.model.entity.Email;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
public class SendEmailJob {

	@Inject
	private transient EmailService emailService;

	@Scheduled(fixedDelay = 1000)
	public void sendEmails() {
		final List<Email> toSend = emailService.findEmailsToSend();
		for (Email email : toSend) {
			try {
				emailService.send(email);
			} catch (Exception e) {
				emailService.updateTries(email);
			}
		}
	}

}
