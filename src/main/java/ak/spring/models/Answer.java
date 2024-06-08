package ak.spring.models;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

    private String description;

    private String attachment;

    @ManyToOne()
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @Lob
    @Column(name = "file", length = 1000)
    private byte[] file;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Person student;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
