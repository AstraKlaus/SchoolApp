package ak.spring.controllers;

import ak.spring.models.Song;
import ak.spring.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/songs")
    public List<Song> getSongs(){
        return songService.findAll();
    }

    @PostMapping("/song")
    public ResponseEntity<Song> uploadSong(@RequestBody Song song){
        return ResponseEntity.status(HttpStatus.OK).body(songService.uploadSong(song));
    }

    @GetMapping("/songId/{id}")
    public Song getSongById(@PathVariable("id") String id){
        return songService.findOne(Integer.parseInt(id));
    }

    @GetMapping("/songName/{name}")
    public Song getSongByName(@PathVariable String name){
        return songService.findByName(name);
    }

    @GetMapping("/song/download/{name}")
    public ResponseEntity<?> downloadAccord (@PathVariable String name){
        Song song = songService.findByName(name);
        if (song!=null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(song);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Запрашиваемой песни не существует, повторите запрос.");
        }
    }
}
