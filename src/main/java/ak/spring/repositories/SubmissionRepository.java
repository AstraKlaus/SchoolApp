package ak.spring.repositories;

import ak.spring.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {

    Optional<Submission> findByFeedbackContainingIgnoreCase(String name);

}
