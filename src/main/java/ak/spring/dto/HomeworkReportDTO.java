package ak.spring.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class HomeworkReportDTO {
    private String homeworkName;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String status;
    private String comment;
    private Timestamp submittedAt;
}
