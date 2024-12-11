package ak.spring.services;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Answer;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.repositories.StatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PersonRepository personRepository;
    private final PersonDTOMapper personDTOMapper;
    private final HomeworkDTOMapper homeworkDTOMapper;
    private final AnswerDTOMapper answerDTOMapper;
    private final HomeworkRepository homeworkRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository,
                         PersonRepository personRepository,
                         PersonDTOMapper personDTOMapper,
                         HomeworkDTOMapper homeworkDTOMapper,
                         AnswerDTOMapper answerDTOMapper,
                         HomeworkRepository homeworkRepository,
                         StatusRepository statusRepository) {
        this.answerRepository = answerRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.homeworkDTOMapper = homeworkDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
        this.homeworkRepository = homeworkRepository;
        this.statusRepository = statusRepository;
    }

    public Page<AnswerDTO> findWithPagination(int offset, int pageSize) {
        return answerRepository.findAll(PageRequest.of(offset, pageSize)).map(answerDTOMapper);
    }

    public AnswerDTO findById(int id) {
        return answerRepository.findById(id).map(answerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }

    public Answer save(AnswerDTO answer) {
        return answerRepository.save(Answer.builder()
                        .attachments(answer.getAttachments())
                        .comment(answer.getComment())
                        .text(answer.getText())
                        .homework(homeworkRepository.findById(answer.getHomeworkId())
                                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", answer.getHomeworkId())))
                        .student(personRepository.findById(answer.getStudentId())
                                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", answer.getStudentId())))
                        .status(statusRepository.findById(answer.getStatusId())
                                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", answer.getStatusId())))
                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

    public List<AnswerDTO> findAll() {
        return answerRepository.findAll()
                .stream()
                .map(answerDTOMapper)
                .toList();
    }

    public void delete(int id) {
        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        answerRepository.delete(existingAnswer);
    }
    public AnswerDTO update(int id, AnswerDTO updatedAnswer) {
        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        existingAnswer.setComment(updatedAnswer.getComment());
        existingAnswer.setAttachments(updatedAnswer.getAttachments());
        existingAnswer.setText(updatedAnswer.getText());
        existingAnswer.setStatus(statusRepository.findById(updatedAnswer.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", updatedAnswer.getStatusId())));
        existingAnswer.setStudent(personRepository.findById(updatedAnswer.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", updatedAnswer.getStudentId())));
        existingAnswer.setHomework(homeworkRepository.findById(updatedAnswer.getHomeworkId())
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", updatedAnswer.getHomeworkId())));
        existingAnswer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        answerRepository.save(existingAnswer);
        return answerDTOMapper.apply(existingAnswer);
    }

    public PersonDTO getStudent(int id) {
        return answerRepository.findById(id)
                .map(Answer::getStudent)
                .map(personDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }

    public HomeworkDTO getHomework(int id) {
        return answerRepository.findById(id)
                .map(Answer::getHomework)
                .map(homeworkDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }

    public AnswerDTO addPersonToAnswer(int id, int personId) {
        return answerRepository.findById(id)
                .map(answer -> {
                    answer.setStudent(personRepository.findById(personId)
                            .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId)));
                    answerRepository.save(answer);
                    return answerDTOMapper.apply(answer);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }
}
