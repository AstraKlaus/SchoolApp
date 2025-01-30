package ak.spring.services;

import ak.spring.dto.GroupProgressDTO;
import ak.spring.dto.HomeworkReportDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Answer;
import ak.spring.models.Classroom;
import ak.spring.models.Homework;
import ak.spring.models.Person;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PersonRepository personRepository;
    private final AnswerRepository answerRepository;
    private final ClassroomRepository classroomRepository;
    private final HomeworkRepository homeworkRepository;

    // Отчет об успеваемости группы
    public List<GroupProgressDTO> generateGroupProgressReport(int classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        // Получаем всех студентов группы
        List<Person> students = personRepository.findByClassroomId(classroomId);

        return students.stream().map(student -> {
            List<Answer> answers = answerRepository.findByStudentId(student.getId());

            long completed = answers.stream()
                    .filter(a -> a.getStatus().getName().equals("Выполнено"))
                    .count();
            long inProgress = answers.stream()
                    .filter(a -> a.getStatus().getName().equals("На проверке"))
                    .count();
            long notCompleted = answers.stream()
                    .filter(a -> a.getStatus().getName().equals("Не выполнено"))
                    .count();

            return GroupProgressDTO.builder()
                    .classroomName(classroom.getName())
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .patronymic(student.getPatronymic())
                    .totalHomeworks(answers.size())
                    .completed((int) completed)
                    .inProgress((int) inProgress)
                    .notCompleted((int) notCompleted)
                    .completionRate(answers.isEmpty() ? 0 : (completed * 100.0) / answers.size())
                    .build();
        }).toList();
    }

    // Отчет по конкретному домашнему заданию
    public List<HomeworkReportDTO> generateHomeworkReport(int homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", homeworkId));

        List<Answer> answers = answerRepository.findByHomeworkId(homeworkId);

        return answers.stream().map(answer -> HomeworkReportDTO.builder()
                .homeworkName(homework.getName())
                .firstName(answer.getStudent().getFirstName())
                .lastName(answer.getStudent().getLastName())
                .patronymic(answer.getStudent().getPatronymic())
                .status(answer.getStatus().getName())
                .comment(answer.getComment())
                .submittedAt(answer.getCreatedAt())
                .build()
        ).toList();
    }
}
