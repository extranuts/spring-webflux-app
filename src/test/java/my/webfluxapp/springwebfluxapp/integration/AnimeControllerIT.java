package my.webfluxapp.springwebfluxapp.integration;


import my.webfluxapp.springwebfluxapp.domain.Anime;
import my.webfluxapp.springwebfluxapp.repository.AnimeRepository;
import my.webfluxapp.springwebfluxapp.service.AnimeService;
import my.webfluxapp.springwebfluxapp.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import javax.management.MXBean;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @return
 */

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import(AnimeService.class)
public class AnimeControllerIT {

    @MockBean
    private AnimeRepository animeRepositoryMock;
    @Autowired
    private WebTestClient testClient;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0); //NoSonar
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    @DisplayName("listAll returns a flux of anime")
    public void listAll_ReturnFluxAnime_WhenSuccessful() {

        testClient
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(anime.getId())
                .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("listAll flavor 2 returns a flux of anime")
    public void listAll_Flavor2_ReturnFluxAnime_WhenSuccessful() {

        testClient
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Anime.class)
                .hasSize(1)
                .contains(anime);
    }
    @Test
    @DisplayName("findById returns a Mono with anime if exists")
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        testClient
                .get()
                .uri("/animes/{id}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    @DisplayName("findById returns a Mono error when anime is not exist")
    public void findById_ReturnMono_Error_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        testClient
                .get()
                .uri("/animes/{id}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A responseStatusException Happened");

    }


}
