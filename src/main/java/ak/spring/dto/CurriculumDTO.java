package ak.spring.dto;

import ak.spring.models.Classroom;
import ak.spring.models.Course;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurriculumDTO {
    private int id;
    private String name;
    private String description;
    private Boolean access;
}