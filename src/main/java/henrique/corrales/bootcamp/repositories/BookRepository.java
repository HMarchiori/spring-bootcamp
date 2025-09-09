package henrique.corrales.bootcamp.repositories;

import henrique.corrales.bootcamp.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {}