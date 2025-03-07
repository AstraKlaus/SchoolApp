package ak.spring.repositories;

import ak.spring.models.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends AttachableRepository<Answer, Integer> {
    List<Answer> findByStudentId(int studentId);
    List<Answer> findByHomeworkId(int homeworkId);
    Page<Answer> findByHomeworkId(int homeworkId, Pageable pageable);

}
