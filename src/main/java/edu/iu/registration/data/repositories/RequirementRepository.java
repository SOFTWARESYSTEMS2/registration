package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Major;
import edu.iu.registration.data.entities.Minor;
import edu.iu.registration.data.entities.Requirement;
import edu.iu.registration.data.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    List<Requirement> findByMajor(Major major);
    List<Requirement> findByMinor(Minor minor);
    List<Requirement> findBySpecialization(Specialization specialization);
}
