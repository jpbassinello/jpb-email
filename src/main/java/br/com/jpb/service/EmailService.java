package br.com.jpb.service;

import br.com.jpb.dao.GenericDao;
import br.com.jpb.model.entity.Email;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
public class EmailService {

	private static final int MAX_TRIES = 3;

	@Inject
	private transient JavaMailSenderImpl mailSender;

	@Inject
	private transient GenericDao genericDao;

	@Inject
	private transient Environment environment;

	private String emailFrom;
	private String nameFrom;

	@PostConstruct
	public void init() {
		this.emailFrom = environment.getProperty("email.from");
		this.nameFrom = environment.getProperty("name.from");
	}

	@Transactional
	public void saveEmailAndSend(Email email) {
		genericDao.persist(email);
	}

	public List<Email> findEmailsToSend() {
		final StringBuilder query = new StringBuilder();
		query.append("select e from email ");
		query.append("where e.sent = false and e.tries < :max");
		return genericDao.getEm().createQuery(query.toString(), Email.class)
				.setParameter("max", MAX_TRIES).getResultList();
	}

	@Transactional
	void updateTries(Email email) {
		email.addTry();
		genericDao.merge(email);
	}

	@Transactional
	void send(Email email) throws MessagingException, IOException {
		// TODO: block email send while not in production

		MimeMessage message = mailSender.createMimeMessage();
		message.setFrom(new InternetAddress(emailFrom, nameFrom));
		message.addRecipients(Message.RecipientType.TO, new Address[]{new
				InternetAddress(email.getEmailTo(), email.getNameTo())});
		message.setSubject(email.getSubject());

		Multipart multipart = new MimeMultipart();
		MimeBodyPart bodyText = new MimeBodyPart();
		bodyText.setContent(email.getBody(), "text/html; charset=utf-8");
		multipart.addBodyPart(bodyText);
		message.setContent(multipart);

		mailSender.send(message);

		email.setSentDateTime(new Date());
		email.setSent(true);
		genericDao.merge(email);
	}
}