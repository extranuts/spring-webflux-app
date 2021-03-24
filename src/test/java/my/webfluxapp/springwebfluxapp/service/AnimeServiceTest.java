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

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSave()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class
        ))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("listAll returns a flux of anime")
    public void listAll_ReturnFluxAnime_WhenSuccessful() {
        StepVerifier.create(animeService.findAll())
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a Mono with anime if exists")
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        StepVerifier.create(animeService.findByID(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("listAll returns a Mono error when anime is not exist")
    public void delete_ReturnMonoError_WhenEmptyIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(animeService.findByID(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates an anime when successful")
    public void save_CreateAnime_WhenSuccessful() {

        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSave();

        StepVerifier.create(animeService.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }
    @Test
    @DisplayName("delete removes a Mono with anime if exists")
    public void delete_RemovesAnime_WhenSuccessful() {
        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }
    @Test
    @DisplayName("delete returns Mono error when anime does not exists")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }




}
