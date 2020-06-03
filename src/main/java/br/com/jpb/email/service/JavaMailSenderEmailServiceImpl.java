package br.com.jpb.email.service;

import br.com.jpb.email.JpbEmailProperties;
import br.com.jpb.email.exception.EmailProviderException;
import br.com.jpb.email.model.entity.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
@Slf4j
@ConditionalOnProperty({
		"jpb.email.active",
		"spring.mail.username",
		"spring.mail.password"
})
@RequiredArgsConstructor
class JavaMailSenderEmailServiceImpl implements ProviderEmailService {

	private final JavaMailSenderImpl mailSender;
	private final JpbEmailProperties jpbEmailProperties;

	@Override
	public void sendToProvider(Email email) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			message.setFrom(new InternetAddress(jpbEmailProperties.getEmailFrom(), jpbEmailProperties.getNameFrom()));
			message.addRecipients(Message.RecipientType.TO,
					new Address[]{new InternetAddress(email.getEmailTo(), email.getNameTo())});
			message.setSubject(email.getSubject());

			Multipart multipart = new MimeMultipart();
			MimeBodyPart bodyText = new MimeBodyPart();
			bodyText.setContent(email.getBody(), "text/html; charset=utf-8");
			multipart.addBodyPart(bodyText);
			message.setContent(multipart);

			mailSender.send(message);
		} catch (Exception e) {
			throw new EmailProviderException(e.getMessage(), e, 500);
		}
	}

}
