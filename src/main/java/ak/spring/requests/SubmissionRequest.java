package ak.spring.requests;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private String feedback;
    private byte[] file;
}
