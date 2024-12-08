package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDTO {
    private int id;
    private String name;
    private String description;
    private List<String> attachments;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
