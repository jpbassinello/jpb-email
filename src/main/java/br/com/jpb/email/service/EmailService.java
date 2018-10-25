package br.com.jpb.email.service;

import br.com.jpb.email.model.entity.Email;
import br.com.jpb.email.repository.EmailRepository;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmailService {

	private static final int MAX_TRIES = 3;

	@Value("${email.default.from}")
	private String emailFrom;

	@Value("${email.default.name}")
	private String nameFrom;

	@Value("${email.app.allowed.emails}")
	private String allowedEmails;

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private EmailRepository repository;

	@Transactional
	public void saveEmailAndSend(Email email) {
		repository.save(email);
	}

	public List<Email> findEmailsToSend() {
		return repository.findBySentAndTriesLessThan(false, MAX_TRIES);
	}

	@Transactional
	public void updateTries(Email email) {
		email.addTry();
		repository.save(email);
	}

	@Transactional
	public void send(Email email) throws MessagingException, IOException {
		log.info("Sending email {} to {}", email.getSubject(), email.getEmailTo());
		if (!isAllowed(email.getEmailTo())) {
			markSent(email);
			return;
		}
		MimeMessage message = mailSender.createMimeMessage();
		message.setFrom(new InternetAddress(emailFrom, nameFrom));
		message.addRecipients(Message.RecipientType.TO,
				new Address[]{new InternetAddress(email.getEmailTo(), email.getNameTo())});
		message.setSubject(email.getSubject());

		Multipart multipart = new MimeMultipart();
		MimeBodyPart bodyText = new MimeBodyPart();
		bodyText.setContent(email.getBody(), "text/html; charset=utf-8");
		multipart.addBodyPart(bodyText);
		message.setContent(multipart);

		mailSender.send(message);

		markSent(email);
	}

	private void markSent(Email email) {
		email.setSentDateTime(LocalDateTime.now());
		email.setSent(true);
		repository.save(email);
	}

	private boolean isAllowed(String emailTo) {
		if ("*".equals(allowedEmails)) {
			return true;
		}
		return Sets
				.newHashSet(Splitter
						.on(",")
						.split(allowedEmails))
				.contains(emailTo);
	}

}
