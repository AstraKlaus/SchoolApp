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
import ak.spring.repositories.PersonRepository;
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

    @Autowired
    public AnswerService(AnswerRepository answerRepository,
                         PersonRepository personRepository, PersonDTOMapper personDTOMapper, HomeworkDTOMapper homeworkDTOMapper, AnswerDTOMapper answerDTOMapper) {
        this.answerRepository = answerRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.homeworkDTOMapper = homeworkDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
    }

    public Page<AnswerDTO> findWithPagination(int offset, int pageSize) {
        return answerRepository.findAll(PageRequest.of(offset, pageSize)).map(answerDTOMapper);
    }

    public AnswerDTO findById(int id) {
        return answerRepository.findById(id).map(answerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }

    public AnswerDTO save(Answer answer) {
        answer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        answerRepository.save(answer);
        return answerDTOMapper.apply(answer);
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
        existingAnswer.setAttachment(updatedAnswer.getAttachment());
        existingAnswer.setText(updatedAnswer.getText());
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
