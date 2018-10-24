package br.com.jpb.email.repository;

import br.com.jpb.email.model.entity.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends CrudRepository<Email, Long> {

	List<Email> findBySentAndTriesLessThan(boolean sent, int tries);

}
