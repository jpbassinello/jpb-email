package br.com.jpb.email.service;

import br.com.jpb.email.JpbEmailProperties;
import br.com.jpb.email.model.entity.Email;
import br.com.jpb.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("jpb.email.active")
public class EmailService {

	private final JpbEmailProperties jpbEmailProperties;
	private final EmailRepository repository;
	private final ProviderEmailService providerEmailService;

	@Transactional
	public void saveEmailAndSend(Email email) {
		repository.save(email);
	}

	List<Email> findEmailsToSend() {
		return repository.findBySentAndTriesLessThan(false, jpbEmailProperties.getMaxTries());
	}

	@Transactional
	void updateTries(Email email) {
		email.addTry();
		repository.save(email);
	}

	@Transactional
	void send(Email email) {
		log.info("Sending email {} to {}", email.getSubject(), email.getEmailTo());
		if (!isAllowed(email.getEmailTo())) {
			markSent(email);
			return;
		}

		providerEmailService.sendToProvider(email);

		markSent(email);
	}

	private void markSent(Email email) {
		email.setSentDateTime(LocalDateTime.now());
		email.setSent(true);
		repository.save(email);
	}

	private boolean isAllowed(String emailTo) {
		if ("*".equals(jpbEmailProperties.getAllowedEmails())) {
			return true;
		}
		return Stream
				.of(jpbEmailProperties
						.getAllowedEmails()
						.split(","))
				.anyMatch(e -> e.equalsIgnoreCase(emailTo));
	}

}
