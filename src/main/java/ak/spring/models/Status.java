package ak.spring.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "homework_status")
public class
Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название статуса не может быть пустым")
    @Size(min = 2, max = 50, message = "Название статуса должно содержать от 2 до 50 символов")
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;
}
