package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private int id;
    private String name;
    private String content;
    private List<String> attachments;
    private String description;
    private boolean access;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
