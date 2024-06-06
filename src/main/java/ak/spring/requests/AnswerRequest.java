package ak.spring.requests;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private String feedback;
    private byte[] file;
}
