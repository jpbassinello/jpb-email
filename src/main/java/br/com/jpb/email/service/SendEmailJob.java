package br.com.jpb.email.service;

import br.com.jpb.email.model.entity.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SendEmailJob {

	@Value("${email.app.sendEmail}")
	private String appSendEmail;

	@Autowired
	private EmailService emailService;

	@Scheduled(fixedDelay = 30_000)
	public void sendEmails() {
		if (!isAppSendEmail()) {
			return;
		}
		for (Email email : emailService.findEmailsToSend()) {
			try {
				emailService.send(email);
			} catch (Exception e) {
				log.warn("Error in SendEmailJob. Email Subject: {}. Email To: {}",
						email.getSubject(), email.getEmailTo(), e);
				emailService.updateTries(email);
			}
		}
	}

	private boolean isAppSendEmail() {
		return "true".equals(appSendEmail);
	}

}
