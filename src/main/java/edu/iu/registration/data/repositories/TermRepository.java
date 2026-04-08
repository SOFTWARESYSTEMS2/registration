package edu.iu.registration.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.Term;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByActiveTrue();
}