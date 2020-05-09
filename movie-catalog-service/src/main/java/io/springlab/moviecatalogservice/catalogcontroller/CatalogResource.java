package io.springlab.moviecatalogservice.catalogcontroller;

import io.springlab.moviecatalogservice.model.MovieCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/movie")
public class CatalogResource {

    private static Logger log= LoggerFactory.getLogger(CatalogResource.class);

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private WebClient.Builder builder;

    @RequestMapping(path = "catalog/{userId}",method = RequestMethod.GET)
    public List<MovieCatalog> getCatalog(@PathVariable("userId") String userId){

        UserRating userRating=restTemplate.getForObject("http://rating-data-service/ratings/users/"+userId,
                UserRating.class);
        log.info("ratingdata {}",userRating.getUserRating());



        //call movie-info-service with rating.movieId()
        return  userRating.getUserRating().stream().map(rating->{

           Movie movie= restTemplate.getForObject("http://movie-info-service/movies/info/"+rating.getMovieId(),Movie.class);
            /*Movie movie=builder
                            .build()
                            .get()
                            .uri("http://movie-info-service/movies/info/"+rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();*/

            log.info("get movie info from {}",movie.getMovieId());
            return new MovieCatalog( movie.getMovieName(),"abcd",rating.getRating());
        }
        ).collect(Collectors.toList());

       // return Collections.singletonList(new MovieCatalog("ABCD","Test",5));
    }
}
