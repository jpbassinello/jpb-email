package br.com.jpb.service;

import br.com.jpb.model.entity.Email;
import br.com.jpb.model.entity.QEmail;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Named
@Singleton
public class EmailService extends GenericService<Email> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	private static final int MAX_TRIES = 3;

	@Value("${email.from}")
	private String EMAIL_FROM;

	@Value("${name.from}")
	private String NAME_FROM;

	@Value("${allowed.emails}")
	private String ALLOWED_EMAILS;

	@Inject
	private transient JavaMailSenderImpl mailSender;

	@Transactional
	public void saveEmailAndSend(Email email) {
		persist(email);
	}

	public List<Email> findEmailsToSend() {
		QEmail email = QEmail.email;
		JPAQuery<Email> query = createJPAQuery().from(email);

		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(email.sent.eq(false));
		booleanBuilder.and(email.tries.lt(MAX_TRIES));

		return query.where(booleanBuilder).fetch();
	}

	@Transactional
	public void updateTries(Email email) {
		email.addTry();
		save(email);
	}

	@Transactional
	public void send(Email email) throws MessagingException, IOException {
		LOGGER.info("Sending email {} to {}", email.getSubject(), email.getEmailTo());
		if (!isAllowed(email.getEmailTo())) {
			markSent(email);
			return;
		}
		MimeMessage message = mailSender.createMimeMessage();
		message.setFrom(new InternetAddress(EMAIL_FROM, NAME_FROM));
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
		email.setSentDateTime(new Date());
		email.setSent(true);
		save(email);
	}

	private boolean isAllowed(String emailTo) {
		if ("*".equals(ALLOWED_EMAILS)) {
			return true;
		}
		return Sets.newHashSet(Splitter.on(",").split(ALLOWED_EMAILS)).contains(emailTo);
	}

}