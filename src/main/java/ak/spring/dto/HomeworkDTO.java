package ak.spring.dto;

import ak.spring.models.Lesson;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDTO {
    private int id;
    private String name;
    private String description;
    private String attachment;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
