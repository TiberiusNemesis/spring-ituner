package com.solo.learning.tdourado.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.learning.tdourado.api.model.ArtistResponse;
import com.solo.learning.tdourado.persistence.repository.ArtistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ArtistService {
  private final ArtistRepository artistRepository;
  private final RestTemplate restTemplate;

  // URLs.
  @Value("${search.url}")
  private String iTunesSearch;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public ArtistService(ArtistRepository artistRepository, RestTemplate restTemplate) {
    this.artistRepository = artistRepository;
    this.restTemplate = restTemplate;
  }

  /**
   * Makes a request to iTunes for all artists with a name that closely match the provided
   * artistName.
   *
   * @param artistName The to-be-queried name.
   * @return An ArtistResponse object containing a list of Artists from iTunes.
   * @throws JsonProcessingException If there are any errors processing the response from iTunes.
   */
  public ArtistResponse fetchArtistsFromItunes(String artistName) throws JsonProcessingException {
    // The complete search URL, constructed by inserting the chosen artist's name on the iTunes
    // address.
    String fullSearchUrl = String.format(iTunesSearch, artistName);

    // Then, Spring's RestTemplate executes the request and stores the result in a String.
    String jsonQueryResult = restTemplate.getForObject(fullSearchUrl, String.class);
    log.debug(
        "Query results for artist named {}: {}",
        artistName,
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));

    // This String is parsed by Jackson and returned.
    return objectMapper.readValue(jsonQueryResult, ArtistResponse.class);
  }
}
