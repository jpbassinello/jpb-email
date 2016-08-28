package br.com.jpb.model.entity;

import br.com.jpb.util.DateTimeUtil;
import br.com.jpb.util.ValidationUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "EMAIL")
public class Email implements Serializable {

	private Long id;
	private String emailTo;
	private String nameTo;
	private String subject;
	private String body;
	private Date createdDateTime;
	private Date sentDateTime;
	private boolean sent = false;
	private int tries = 0;

	public Email() {
		this.createdDateTime = DateTimeUtil.nowWithDateTimeInUTC().toDate();
		this.tries = 0;
		this.sent = false;
	}

	public Email(String emailTo, String nameTo, String subject, String body) {
		this();
		this.emailTo = emailTo;
		this.nameTo = nameTo;
		this.subject = subject;
		this.body = body;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMAIL_ID")
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	@Column(name = "EMAIL")
	@NotEmpty(message = "Forneça um endereço de e-mail válido")
	@Size(max = 255, message = "Forneça um endereço de e-mail com no máximo " + "{max} caracteres")
	@Pattern(regexp = ValidationUtil.REGEXP_EMAIL, message = "Forneça um " + "endereço de e-mail válido")
	public String getEmailTo() {
		return emailTo;
	}

	void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	@Column(name = "NAME")
	@NotEmpty(message = "Forneça um nome válido")
	@Size(max = 255, message = "Forneça um nome com no máximo {max} " + "caracteres")
	public String getNameTo() {
		return nameTo;
	}

	void setNameTo(String nameTo) {
		this.nameTo = nameTo;
	}

	@Column(name = "SUBJECT")
	@NotEmpty(message = "Forneça um assunto válido")
	@Size(max = 255, message = "Forneça um assunto com no máximo {max} " + "caracteres")
	public String getSubject() {
		return subject;
	}

	void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "BODY")
	@NotEmpty(message = "Forneça uma mensagem válida")
	@Size(max = 10000, message = "Forneça uma mensagem com no máximo {max} " + "caracteres")
	public String getBody() {
		return body;
	}

	void setBody(String body) {
		this.body = body;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SENT_DATE_TIME")
	public Date getSentDateTime() {
		return sentDateTime;
	}

	public void setSentDateTime(Date sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE_TIME")
	@NotNull
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public int getTries() {
		return tries;
	}

	void setTries(int tries) {
		this.tries = tries;
	}

	public void addTry() {
		this.tries++;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Email)) {
			return false;
		}
		Email other = (Email) obj;
		return Objects.equals(id, other.getId());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}