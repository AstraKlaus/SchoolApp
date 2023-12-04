package ak.spring.controllers;

import ak.spring.models.Author;
import ak.spring.models.Song;
import ak.spring.services.AuthorService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/authorId/{uuid}")
    public Author getSongByUuid(@PathVariable("uuid") UUID uuid){
        return authorService.findByUuid(uuid);
    }


    @GetMapping("/authorSongs/{id}")
    public List<Song> getAuthorSongs(@PathVariable("id") String id){
        Author author = authorService.findById(Integer.parseInt(id));
        if (author==null) return null;
        return authorService.getSongs(author);
    }

    @GetMapping("/authorByName/{name}")
    public List<Author> getAuthorByName(@PathVariable("name") String name){
        return authorService.findByName(name);
    }

    @PatchMapping("/author/{id}")
    public void updateAuthor(@PathVariable("id") String id, Author author){
        authorService.updateAuthor(Integer.parseInt(id), author);
    }

    @DeleteMapping("/author/{id}")
    public void deleteAuthor(@PathVariable("id") String id){
        Author author = authorService.findById(Integer.parseInt(id));
        if (author != null) authorService.deleteAuthor(author);
    }

    @PostMapping("/author/{id}")
    public Author uploadAuthor(Author author){
        return authorService.uploadAuthor(author);
    }

    @GetMapping("/authors")
    public List<Author> getAuthors(){
        return authorService.findAll();
    }
}
