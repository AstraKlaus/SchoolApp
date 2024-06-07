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
    private String comment;
    private String attachment;
    private String description;
    private byte[] file;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
