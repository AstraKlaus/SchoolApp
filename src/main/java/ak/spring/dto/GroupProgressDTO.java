package ak.spring.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupProgressDTO {
    private String classroomName;
    private String firstName;
    private String lastName;
    private String patronymic;
    private int totalHomeworks;
    private int completed;
    private int inProgress;
    private int notCompleted;
    private double completionRate;
}
