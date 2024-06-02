package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private String name;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
