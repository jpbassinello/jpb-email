package br.com.jpb.model.entity;

import br.com.jpb.model.BaseEntity;
import br.com.jpb.util.DateTimeUtil;
import br.com.jpb.util.ValidationUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "EMAIL")
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Email implements BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMAIL_ID")
	private Long id;

	@Column(name = "EMAIL")
	@NotEmpty(message = "Forneça um endereço de e-mail válido")
	@Size(max = 255, message = "Forneça um endereço de e-mail com no máximo {max} caracteres")
	@Pattern(regexp = ValidationUtil.REGEXP_EMAIL, message = "Forneça um endereço de e-mail válido")
	private String emailTo;

	@Column(name = "NAME")
	@NotEmpty(message = "Forneça um nome válido")
	@Size(max = 255, message = "Forneça um nome com no máximo {max} caracteres")
	private String nameTo;

	@Column(name = "SUBJECT")
	@NotEmpty(message = "Forneça um assunto válido")
	@Size(max = 255, message = "Forneça um assunto com no máximo {max} caracteres")
	private String subject;

	@Column(name = "BODY")
	@NotEmpty(message = "Forneça uma mensagem válida")
	@Size(max = 1_000_000, message = "Forneça uma mensagem com no máximo {max} caracteres")
	private String body;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE_TIME")
	@NotNull
	private Date createdDateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SENT_DATE_TIME")
	@Setter
	private Date sentDateTime;

	@Column
	@Setter
	private boolean sent = false;

	@Column
	private int tries = 0;

	public Email() {
		this.createdDateTime = DateTimeUtil
				.nowWithDateTimeInUTC()
				.toDate();
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

	public void addTry() {
		this.tries++;
	}

}