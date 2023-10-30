package ak.spring.controllers;


import ak.spring.models.Person;
import ak.spring.services.SongService;
import ak.spring.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SongService songService;
    private final PeopleService peopleService;

    @Autowired
    public AdminController(SongService songService, PeopleService peopleService) {
        this.songService = songService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model){
        model.addAttribute("books", songService.findAll());
        model.addAttribute("people", peopleService.findAll());
        return "/admin/index";
    }

    @DeleteMapping()
    public String delete(@RequestParam(name = "person") String person_id, @RequestParam(name = "book_id") String book_id) {
        System.out.println("here9");
        peopleService.getSongsByPersonId(Integer.parseInt(person_id)).remove(songService.findOne(Integer.parseInt(book_id)));
        peopleService.save(peopleService.findOne(Integer.parseInt(person_id)));
        songService.save(songService.findOne(Integer.parseInt(book_id)));
        return "redirect:/admin";
    }

    @DeleteMapping("/books")
    public String deleteBook(@RequestParam(name = "book_id") String book_id) {
        System.out.println("here9");
        peopleService.deleteSong(Integer.parseInt(book_id));
        songService.delete(Integer.parseInt(book_id));
        return "redirect:/admin/books";
    }

    @GetMapping("/people")
    public String people(Model model){
        model.addAttribute("people", peopleService.findAll());
        return "admin/people";
    }

    @DeleteMapping("/people")
    public String deletePerson(@RequestParam(name = "person_id") String person_id) {
        System.out.println("here9");
        peopleService.delete(Integer.parseInt(person_id));
        return "redirect:/admin/people";
    }

    @GetMapping("/books")
    public String books(Model model){
        model.addAttribute("books", songService.findAll());
        return "/admin/books";
    }

    @GetMapping("/people/{id}")
    public String person(Model model, @PathVariable int id){
        model.addAttribute("person", peopleService.findOne(id));
        return "admin/edit";
    }

    @PatchMapping("/people/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        System.out.println("here7");
        if (bindingResult.hasErrors())
            return "/admin/edit";

        peopleService.update(id, person);
        return "redirect:/admin/people";
    }
}
