package ak.spring.controllers;

import ak.spring.models.Accord;
import ak.spring.models.Song;
import ak.spring.services.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {this.songService = songService;}

    @GetMapping("/songs")
    public List<Song> getSongs(){
        return songService.findAll();
    }

    @PostMapping("/song")
    public Song uploadSong(@RequestBody Song song){
        return songService.uploadSong(song);
    }

    @GetMapping("/songId/{uuid}")
    public Song getSongByUuid(@PathVariable("uuid") UUID uuid){
        return songService.findByUuid(uuid);
    }

    @GetMapping("/songName/{name}")
    public List<Song> getSongByName(@PathVariable String name){
        return songService.findByName(name);
    }

    @GetMapping("/song/download/{name}")
    public ResponseEntity<?> downloadAccord (@PathVariable String name){
        List<Song> songs = songService.findByName(name);
        if (songs!=null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(songs);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Запрашиваемой песни не существует, повторите запрос.");
        }
    }

    @PatchMapping("/song/{id}")
    public void updateSong(@PathVariable("id") String id, Song songForUpdate){
        songService.updateSong(Integer.parseInt(id), songForUpdate);
    }

    @DeleteMapping("/song/{id}")
    public void deleteSong(@PathVariable("id") String id){
        Song song = songService.findById(Integer.parseInt(id));
        if (song != null) songService.deleteSong(song);
    }

    @GetMapping("/song/{uuid}/accords")
    public List<Accord> getSongAccords(@PathVariable("uuid") UUID id){
        return songService.getAccords(id);
    }
}
