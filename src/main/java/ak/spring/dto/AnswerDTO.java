package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private int id;
    private String text;
    private String comment;
    private String attachment;
    private int studentId;
    private int homeworkId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
