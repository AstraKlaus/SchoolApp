package ak.spring.repositories;

import ak.spring.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends
        AttachableRepository<Answer, Integer> {
}
