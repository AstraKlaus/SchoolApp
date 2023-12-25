package ak.spring.controllers;

import ak.spring.models.Accord;
import ak.spring.models.Author;
import ak.spring.services.AccordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class AccordController {


    private final AccordService accordService;

    @Autowired
    public AccordController(AccordService accordService) {
        this.accordService = accordService;
    }

    @GetMapping("/accords/{offset}/{pageSize}")
    public Page<Accord> getAuthorsWithPagination(@PathVariable int offset, @PathVariable int pageSize){
        return accordService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/accordId/{uuid}")
    public Accord getAccordByUuid(@PathVariable("uuid") UUID uuid){
        return accordService.findByUuid(uuid);
    }

    @PostMapping("/accord")
    public Accord uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return accordService.uploadAccord(file);
    }

    @PatchMapping("/accord/{id}")
    public Accord updateImage(@PathVariable("id") String id, @RequestParam("name") String name, @RequestParam("image") MultipartFile file) throws IOException {
        return accordService.updateAccord(Integer.parseInt(id), name, file.getBytes());
    }

    @PostMapping("/accords")
    public List<Accord> uploadImages(@RequestParam("images") MultipartFile[] files) throws IOException {
        List<Accord> newAccords = new ArrayList<>();
        for (MultipartFile file:files){
            newAccords.add(accordService.uploadAccord(file));
        }
        return newAccords;
    }

    @GetMapping("/accord/{name}")
    public ResponseEntity<?> downloadAccord (@PathVariable String name){
        byte[] accord = accordService.downloadAccord(name);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(accord);
    }

    @GetMapping("/accords")
    public List<Accord> getAccords(){
        return accordService.findAll();
    }

    @DeleteMapping("/accord/{id}")
    public void deleteAccord(@PathVariable("id") String id){
        Accord accord = accordService.findById(Integer.parseInt(id));
        if (accord != null) accordService.deleteAccord(accord);
    }
}
