package com.solo.learning.tdourado.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.learning.tdourado.api.model.AlbumResponse;
import com.solo.learning.tdourado.api.model.ArtistResponse;
import com.solo.learning.tdourado.persistence.domain.Artist;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Controller for the lookups and searches using the iTunes API.
 *
 * @author tiberiusdourado
 */
@Slf4j
@RestController
@RequestMapping("/artist")
public class TunesController {

  // Response objects.
  private AlbumResponse albumResponse;
  private ArtistResponse artistResponse;

  // Spring.
  @Setter private RestTemplate restTemplate = new RestTemplate();

  // Jackson ObjectMapper.
  private final ObjectMapper objectMapper = new ObjectMapper();

  // URLs.
  @Value("${search.url}")
  private String iTunesSearch;

  @Value("${lookup.url}")
  private String iTunesLookup;

  /**
   * Using a valid iTunes artist ID, makes a request to the API. Then returns a JSON list of all
   * albums found associated to an artist.
   *
   * @param artistId A valid artist ID in the iTunes store.
   * @return A ResponseEntity containing the results of the query.
   */
  @GetMapping("/{id}/albums")
  public ResponseEntity<AlbumResponse> fetchAlbumsByArtistId(
      @PathVariable("id") final @NotNull String artistId) {
    try {
      albumResponse = fetchAlbumsFromItunes(artistId);
    } catch (JsonProcessingException | RestClientException exception) {
      log.debug("Error while processing the Album request: {}", exception.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(albumResponse, HttpStatus.OK);
  }

  /**
   * Makes a request to iTunes for all albums associated to an ArtistId.
   *
   * @param artistId A valid artist ID in the iTunes store.
   * @return An AlbumResponse object containing an Artist and a list of Albums associated to this
   *     artist.
   * @throws JsonProcessingException If there are any errors processing the JSON response from
   *     iTunes.
   */
  private AlbumResponse fetchAlbumsFromItunes(final @NotNull String artistId)
      throws JsonProcessingException, RestClientException {
    albumResponse = new AlbumResponse();
    // The full lookup URL, constructed by inserting the chosen artist's ID on the iTunes address.
    final String fullLookupUrl = String.format(iTunesLookup, artistId);
    String jsonQueryResult = restTemplate.getForObject(fullLookupUrl, String.class);
    log.debug(
        "Query results for artist ID {}: {}",
        artistId,
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));

    // However, the first result of this lookup is an artist.
    Artist albumAuthor =
        objectMapper.readValue(
            objectMapper.writeValueAsString(albumResponse.getResults().get(0)), Artist.class);

    // Jackson doesn't really handle this, so it needs to be done manually.
    albumResponse.setArtist(albumAuthor);
    albumResponse.getResults().remove(0);
    return albumResponse;
  }

  /**
   * Using a name, makes a request to the iTunes API and returns a JSON list of music artists with a
   * similar name.
   *
   * @param artistName The to-be-queried name.
   * @return A ResponseEntity containing the results of the query.
   */
  @GetMapping
  public ResponseEntity<ArtistResponse> fetchArtistsByName(
      @RequestParam("term") final @NotNull String artistName) {
    try {
      artistResponse = fetchArtistsFromItunes(artistName);
    } catch (JsonProcessingException | RestClientException exception) {
      log.debug("Error while processing the Artist request: {}", exception.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(artistResponse, HttpStatus.OK);
  }

  /**
   * Makes a request to iTunes for all artists with a name that closely match the provided
   * artistName.
   *
   * @param artistName The to-be-queried name.
   * @return An ArtistResponse object containing a list of Artists from iTunes.
   * @throws JsonProcessingException If there are any errors processing the response from iTunes.
   */
  private ArtistResponse fetchArtistsFromItunes(String artistName) throws JsonProcessingException {
    artistResponse = new ArtistResponse();
    // The complete search URL, constructed by inserting the chosen artist's name on the iTunes
    // address.
    String fullSearchUrl = String.format(iTunesSearch, artistName);

    // Then, Spring's RestTemplate executes the request and stores the result in a String.
    String jsonQueryResult = restTemplate.getForObject(fullSearchUrl, String.class);
    log.debug(
        "Query results for artist named {}: {}",
        artistName,
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));

    // This String is parsed by Jackson, then returned.
    artistResponse = objectMapper.readValue(jsonQueryResult, ArtistResponse.class);
    return artistResponse;
  }
}
