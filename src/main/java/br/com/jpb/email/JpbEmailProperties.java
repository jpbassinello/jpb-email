package br.com.jpb.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("jpb.email")
public class JpbEmailProperties {

	private boolean active = false;
	private int maxTries;
	private String emailFrom;
	private String nameFrom;
	private String allowedEmails;

}
