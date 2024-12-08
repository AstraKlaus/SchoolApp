package ak.spring.models;


import ak.spring.mappers.StringListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "lesson")
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<String> attachments;

    @Column(name = "description")
    private String description;

    @Column(name = "access")
    private boolean access;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
