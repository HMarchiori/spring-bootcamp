package henrique.corrales.bootcamp.repositories;

import henrique.corrales.bootcamp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
