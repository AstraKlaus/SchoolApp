package ak.spring.controllers;

import ak.spring.models.Author;
import ak.spring.models.Service;
import ak.spring.models.Song;
import ak.spring.services.AuthorService;
import ak.spring.services.SongService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class ServiceController {
    private final SongService songService;
    private final AuthorService authorService;

    public ServiceController(SongService songService, AuthorService authorService) {
        this.songService = songService;
        this.authorService = authorService;
    }

    @GetMapping("/search/{name}")
    public Service findAuthorAndSong(@PathVariable("name") String search){
        List<Author> authors = authorService.findByName(search);
        List<Song> songs = songService.findByName(search);
        for (Author author : authors) {
            songs.addAll(authorService.getSongs(author));
        }
        return new Service(authors, songs);
    }
}
