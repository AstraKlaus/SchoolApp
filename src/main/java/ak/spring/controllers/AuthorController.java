package ak.spring.controllers;

import ak.spring.models.Author;
import ak.spring.models.Song;
import ak.spring.services.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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


    @GetMapping("/authorSongs/{uuid}")
    public List<Song> getAuthorSongs(@PathVariable("uuid") UUID id){
        Author author = authorService.findByUuid(id);
        if (author==null) return null;
        return authorService.getSongs(author);
    }

    @GetMapping("/authorByName/{name}")
    public List<Author> getAuthorByName(@PathVariable("name") String name){
        return authorService.findByName(name);
    }

    @GetMapping("/authors/{offset}/{pageSize}")
    public Page<Author> getAuthorsWithPagination(@PathVariable int offset, @PathVariable int pageSize){
        return authorService.findWithPagination(offset, pageSize);
    }

    @PatchMapping("/author/{id}")
    public Author updateAuthor(@PathVariable("id") String id, @RequestBody Author author){
        return authorService.updateAuthor(Integer.parseInt(id), author);
    }

    @DeleteMapping("/author/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable("id") String id){
        Author author = authorService.findById(Integer.parseInt(id));
        if (author != null && author.getSongs().isEmpty()) {
            authorService.deleteAuthor(author);
            return new ResponseEntity<>("Автор успешно удален", HttpStatus.OK);
        } else return new ResponseEntity<>("Нельзя удалить автора, который имеет песню", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/author")
    public Author uploadAuthor(@RequestBody Author author){
        return authorService.uploadAuthor(author);
    }

    @GetMapping("/authors")
    public List<Author> getAuthors(){
        return authorService.findAll();
    }
}
