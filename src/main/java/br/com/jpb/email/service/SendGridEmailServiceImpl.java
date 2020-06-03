package br.com.jpb.email.service;

import br.com.jpb.email.JpbEmailProperties;
import br.com.jpb.email.exception.EmailProviderException;
import br.com.jpb.email.model.entity.Email;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@ConditionalOnProperty({
		"jpb.email.active",
		"spring.sendgrid.api-key"
})
@RequiredArgsConstructor
class SendGridEmailServiceImpl implements ProviderEmailService {

	private final SendGrid sendGrid;
	private final JpbEmailProperties jpbEmailProperties;

	@Override
	public void sendToProvider(Email email) {
		Mail mail = new Mail(new com.sendgrid.helpers.mail.objects.Email(jpbEmailProperties.getEmailFrom(),
				jpbEmailProperties.getNameFrom()),
				email.getSubject(),
				new com.sendgrid.helpers.mail.objects.Email(email.getEmailTo(), email.getNameTo()),
				new Content("text/html", email.getBody()));

		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = this.sendGrid.api(request);

			boolean success = response.getStatusCode() >= 200 && response.getStatusCode() < 300;

			if (!success) {
				throw new EmailProviderException(response.getBody(), response.getStatusCode());
			}
		} catch (IOException ex) {
			throw new EmailProviderException("Unexpected sendgrid api error", ex, 500);
		}
	}
}
