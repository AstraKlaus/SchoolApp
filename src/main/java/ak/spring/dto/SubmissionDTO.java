package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private PersonDTO student;
    private String feedback;
    private byte[] file;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
