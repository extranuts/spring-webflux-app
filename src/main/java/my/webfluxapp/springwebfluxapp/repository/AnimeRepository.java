package my.webfluxapp.springwebfluxapp.repository;


import my.webfluxapp.springwebfluxapp.domain.Anime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author
 * @return
 */

@Repository
public interface AnimeRepository extends ReactiveCrudRepository<Anime, Long> {

    Mono<Anime> findById(int id);
}
