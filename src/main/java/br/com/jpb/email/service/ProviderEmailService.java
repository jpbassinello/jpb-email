package br.com.jpb.email.service;

import br.com.jpb.email.model.entity.Email;

interface ProviderEmailService {

	void sendToProvider(Email email);

}
