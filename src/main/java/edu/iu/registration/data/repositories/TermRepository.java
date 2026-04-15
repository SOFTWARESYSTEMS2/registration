package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByLabel(String label);
    Optional<Term> findByActiveTrue();
    List<Term> findAllByOrderByIdAsc();
}
