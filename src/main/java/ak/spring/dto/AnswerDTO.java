package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private int id;
    private String text;
    private String comment;
    private List<String> attachments;
    private int studentId;
    private int homeworkId;
    private int statusId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
