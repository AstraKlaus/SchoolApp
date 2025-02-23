package ak.spring.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "letter_spacing")
public class LetterSpacing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название межбуквенного интервала не может быть пустым")
    @Size(min = 2, max = 50, message = "Название размера шрифта должно содержать от 2 до 50 символов")
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;
}
