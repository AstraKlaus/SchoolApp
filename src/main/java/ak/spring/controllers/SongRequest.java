package ak.spring.controllers;

import ak.spring.models.Accord;
import ak.spring.models.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongRequest {

    private String name;
    String text;
    Author author;
    List<Accord> accords;
}