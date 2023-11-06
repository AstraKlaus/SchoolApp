package ak.spring.controllers;

import ak.spring.models.Accord;
import ak.spring.services.AccordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class AccordController {


    private final AccordService accordService;

    @Autowired
    public AccordController(AccordService accordService) {
        this.accordService = accordService;
    }

    @PostMapping("/accord")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadAccord = accordService.uploadAccord(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadAccord);
    }

    @PatchMapping("/accord/{id}")
    public void updateImage(@PathVariable("id") String id, String name ,@RequestParam("image") MultipartFile file) throws IOException {
        accordService.updateAccord(Integer.parseInt(id),name, file);
    }

    @PostMapping("/accords")
    public ResponseEntity<?> uploadImages(@RequestParam("images") List<MultipartFile> files) throws IOException {
        for (MultipartFile file:files){
            accordService.uploadAccord(file);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Successfuly uploaded " + files.size() + " accords");
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

}
