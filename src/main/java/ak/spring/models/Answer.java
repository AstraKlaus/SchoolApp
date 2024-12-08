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
@Table(name = "answer")
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String comment;

    private String text;

    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<String> attachments;

    @ManyToOne()
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Person student;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
