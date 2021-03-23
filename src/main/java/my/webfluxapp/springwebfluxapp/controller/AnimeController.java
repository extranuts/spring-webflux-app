package my.webfluxapp.springwebfluxapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.webfluxapp.springwebfluxapp.service.AnimeService;
import my.webfluxapp.springwebfluxapp.domain.Anime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author
 * @return
 */

@RestController
@RequestMapping("animes")
@Slf4j
@RequiredArgsConstructor
public class AnimeController {


    private final AnimeService animeService;

    @GetMapping
    public Flux<Anime> listAll(){
        return animeService.findAll();
    }
    @GetMapping(path = "{id}")
    public Mono<Anime> findById(@PathVariable int id){
        return animeService.findByID(id);
    }
}
