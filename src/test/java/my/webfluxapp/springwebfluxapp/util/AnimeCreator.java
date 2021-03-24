package my.webfluxapp.springwebfluxapp.util;


import my.webfluxapp.springwebfluxapp.domain.Anime;

/**
 * @author
 * @return
 */


public class AnimeCreator {

    public static Anime createAnimeToBeSave(){
        return Anime.builder()
                .name("Anime 1")
                .build();
    }
    public static Anime createValidAnime(){
        return Anime.builder()
                .id(1)
                .name("Anime 2")
                .build();
    }
    public static Anime createValidUpdateAnime(){
        return Anime.builder()
                .id(1)
                .name("Anime 3")
                .build();
    }

}
