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
@Builder
@Table(name = "font_size")
public class FontSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название размера шрифта не может быть пустым")
    @Size(min = 2, max = 50, message = "Название размера шрифта должно содержать от 2 до 50 символов")
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;
}


