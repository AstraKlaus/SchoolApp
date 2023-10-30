package ak.spring.controllers;

import ak.spring.models.Author;
import ak.spring.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/authorId/{id}")
    public Author getAuthorById(@PathVariable("id") String id){
        return authorService.findById(Integer.parseInt(id));
    }

    @GetMapping("/authorName/{name}")
    public Author getAuthorByName(@PathVariable("name") String name){
        return authorService.findByName(name);
    }
}
