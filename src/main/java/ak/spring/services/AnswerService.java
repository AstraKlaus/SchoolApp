package ak.spring.services;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Answer;
import ak.spring.repositories.AnswerRepository;
import ak.spring.requests.AnswerRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PersonDTOMapper personDTOMapper;
    private final AnswerDTOMapper answerDTOMapper;

    @Autowired
    public AnswerService(AnswerRepository answerRepository,
                         PersonDTOMapper personDTOMapper, AnswerDTOMapper answerDTOMapper) {
        this.answerRepository = answerRepository;
        this.personDTOMapper = personDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
    }

    public Answer uploadAccord(MultipartFile file) throws IOException {
        return answerRepository.save(Answer.builder()
                .file(file.getBytes())
                .build());
    }

    public AnswerDTO updateAccord(int id, byte[] file) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        answer.setFile(file);
        answerRepository.save(answer);
        return answerDTOMapper.apply(answer);
    }

    public Page<AnswerDTO> findWithPagination(int offset, int pageSize) {
        return answerRepository.findAll(PageRequest.of(offset, pageSize)).map(answerDTOMapper);
    }

    public AnswerDTO findById(int id) {
        return answerRepository.findById(id).map(answerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
    }

    public byte[] downloadAccord(String name) {
        return findByName(name).getFile();
    }

    public AnswerDTO findByName(String name) {
        return answerRepository.findByFeedbackContainingIgnoreCase(name).map(answerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "name", name));
    }

    public AnswerDTO save(Answer answer) {
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
    public AnswerDTO update(int id, AnswerRequest updatedSubmission) {
        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        existingAnswer.setFile(updatedSubmission.getFile());
        existingAnswer.setFeedback(updatedSubmission.getFeedback());
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
}
