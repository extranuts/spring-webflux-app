package my.webfluxapp.springwebfluxapp.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.webfluxapp.springwebfluxapp.domain.Anime;
import my.webfluxapp.springwebfluxapp.repository.AnimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author
 * @return
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;


    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findByID(int id){
        return animeRepository.findById(id)
                .switchIfEmpty(monoResponseStatusException())
                .log();
    }
    public <T> Mono<T> monoResponseStatusException(){
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not Found"));
    }
}
