package br.com.jpb.email.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "email")
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Email implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	@NotEmpty
	@Size(max = 255)
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@" +
			"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	private String emailTo;

	@Column(name = "name")
	@NotEmpty()
	@Size(max = 255)
	private String nameTo;

	@Column(name = "subject")
	@NotEmpty
	@Size(max = 255)
	private String subject;

	@Column(name = "body")
	@NotEmpty
	@Size(max = 1_000_000)
	private String body;

	@Column(name = "created_date_time")
	@NotNull
	private LocalDateTime createdDateTime;

	@Column(name = "sent_date_time")
	@Setter
	private LocalDateTime sentDateTime;

	@Column
	@Setter
	private boolean sent;

	@Column
	private int tries;

	public Email() {
		this.createdDateTime = LocalDateTime.now();
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
