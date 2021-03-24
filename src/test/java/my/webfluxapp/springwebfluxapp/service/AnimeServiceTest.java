package my.webfluxapp.springwebfluxapp.service;

import my.webfluxapp.springwebfluxapp.domain.Anime;
import my.webfluxapp.springwebfluxapp.repository.AnimeRepository;
import my.webfluxapp.springwebfluxapp.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;


@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
    }

    @Test
    @DisplayName("listAll returns a flux of anime")
    public void listAll_ReturnFluxAnime_WhenSuccessful() {
        StepVerifier.create(animeService.findAll())
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("listAll returns a Mono with anime if exists")
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        StepVerifier.create(animeService.findByID(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("listAll returns a Mono error when anime is not exist")
    public void findById_ReturnMonoError_WhenEmptyIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        StepVerifier.create(animeService.findByID(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }


}
