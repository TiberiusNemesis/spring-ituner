package com.solo.learning.tdourado.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.learning.tdourado.api.model.AlbumResponse;
import com.solo.learning.tdourado.api.model.ArtistResponse;
import com.solo.learning.tdourado.persistence.domain.Album;
import com.solo.learning.tdourado.persistence.domain.Artist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Controller for the lookups and searches using the iTunes API.
 *
 * <p>This controller provides endpoints to search for music artists and retrieve album information
 * from the iTunes Store API. It uses WebClient for reactive, non-blocking HTTP communication with
 * comprehensive error handling and logging.
 *
 * @author tiberiusdourado
 */
@Slf4j
@RestController
@RequestMapping("/artist")
@Tag(
    name = "iTunes API",
    description = "Endpoints for searching artists and retrieving album information from iTunes")
public class TunesController {

  // Response objects.
  private AlbumResponse albumResponse;
  private ArtistResponse artistResponse;

  // WebClient for reactive HTTP calls
  private final WebClient webClient;

  // Jackson ObjectMapper.
  private final ObjectMapper objectMapper = new ObjectMapper();

  // URLs.
  @Value("${search.url}")
  private String iTunesSearch;

  @Value("${lookup.url}")
  private String iTunesLookup;

  /**
   * Constructor that initializes WebClient with timeout configuration.
   *
   * @param webClientBuilder WebClient.Builder provided by Spring
   */
  public TunesController(WebClient.Builder webClientBuilder) {
    this.webClient =
        webClientBuilder
            .codecs(
                configurer ->
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
            .build();
  }

  /**
   * Using a valid iTunes artist ID, makes a request to the API. Then returns a JSON list of all
   * albums found associated to an artist.
   *
   * @param artistId A valid artist ID in the iTunes store.
   * @return A ResponseEntity containing the results of the query.
   */
  @Operation(
      summary = "Get albums by artist ID",
      description =
          "Retrieves all albums for a specific artist using their iTunes artist ID. "
              + "Returns detailed information about the artist and their complete album catalog.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved albums",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AlbumResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid artist ID or iTunes API error",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content)
      })
  @GetMapping("/{id}/albums")
  public ResponseEntity<AlbumResponse> fetchAlbumsByArtistId(
      @Parameter(description = "iTunes artist ID", example = "909253", required = true)
          @PathVariable("id")
          final @NotNull String artistId) {
    log.info("Received request to fetch albums for artist ID: {}", artistId);
    long startTime = System.currentTimeMillis();

    try {
      albumResponse = fetchAlbumsFromItunes(artistId);
      long duration = System.currentTimeMillis() - startTime;
      log.info(
          "Successfully fetched {} albums for artist ID {} in {}ms",
          albumResponse.getResults().size(),
          artistId,
          duration);
      return new ResponseEntity<>(albumResponse, HttpStatus.OK);
    } catch (JsonProcessingException exception) {
      log.error(
          "JSON processing error while fetching albums for artist ID {}: {}",
          artistId,
          exception.getMessage(),
          exception);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (WebClientResponseException exception) {
      log.error(
          "iTunes API error while fetching albums for artist ID {}: HTTP {} - {}",
          artistId,
          exception.getStatusCode(),
          exception.getResponseBodyAsString(),
          exception);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (Exception exception) {
      log.error(
          "Unexpected error while fetching albums for artist ID {}: {}",
          artistId,
          exception.getMessage(),
          exception);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Makes a request to iTunes for all albums associated to an ArtistId using WebClient.
   *
   * @param artistId A valid artist ID in the iTunes store.
   * @return An AlbumResponse object containing an Artist and a list of Albums associated to this
   *     artist.
   * @throws JsonProcessingException If there are any errors processing the JSON response from
   *     iTunes.
   */
  private AlbumResponse fetchAlbumsFromItunes(final @NotNull String artistId)
      throws JsonProcessingException {
    final String fullLookupUrl = String.format(iTunesLookup, artistId);
    log.debug("Making iTunes API request to: {}", fullLookupUrl);

    String jsonQueryResult =
        webClient
            .get()
            .uri(fullLookupUrl)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(10))
            .doOnError(
                error ->
                    log.error(
                        "WebClient error while calling iTunes API for artist ID {}: {}",
                        artistId,
                        error.getMessage()))
            .onErrorResume(
                error -> {
                  log.warn("Retrying iTunes API request for artist ID: {}", artistId);
                  return Mono.error(error);
                })
            .block();

    if (log.isDebugEnabled()) {
      log.debug(
          "iTunes API response for artist ID {}: {}",
          artistId,
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));
    }

    albumResponse = objectMapper.readValue(jsonQueryResult, AlbumResponse.class);

    if (albumResponse.getResults() == null || albumResponse.getResults().isEmpty()) {
      log.warn("No results found for artist ID: {}", artistId);
      return albumResponse;
    }

    // The first result is the artist object
    Artist albumAuthor =
        objectMapper.readValue(
            objectMapper.writeValueAsString(albumResponse.getResults().get(0)), Artist.class);
    albumResponse.setArtist(albumAuthor);

    // Remove the artist from the results list (keeping only albums)
    List<Album> artistLessAlbumList = albumResponse.getResults();
    artistLessAlbumList.remove(0);
    albumResponse.setResults(artistLessAlbumList);

    log.debug(
        "Processed iTunes response: artist='{}', album_count={}",
        albumAuthor.getArtistName(),
        artistLessAlbumList.size());

    return albumResponse;
  }

  /**
   * Using a name, makes a request to the iTunes API and returns a JSON list of music artists with
   * a similar name.
   *
   * @param artistName The to-be-queried name.
   * @return A ResponseEntity containing the results of the query.
   */
  @Operation(
      summary = "Search artists by name",
      description =
          "Searches for music artists in the iTunes Store by name. "
              + "Returns up to 5 matching artists with their basic information. "
              + "Supports partial name matching and fuzzy search.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching artists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ArtistResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search term or iTunes API error",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content)
      })
  @GetMapping
  public ResponseEntity<ArtistResponse> fetchArtistsByName(
      @Parameter(description = "Artist name to search for", example = "Taylor Swift", required = true)
          @RequestParam("term")
          final @NotNull String artistName) {
    log.info("Received request to search for artists with name: '{}'", artistName);
    long startTime = System.currentTimeMillis();

    try {
      artistResponse = fetchArtistsFromItunes(artistName);
      long duration = System.currentTimeMillis() - startTime;
      log.info(
          "Successfully found {} artists matching '{}' in {}ms",
          artistResponse.getResults().size(),
          artistName,
          duration);
      return new ResponseEntity<>(artistResponse, HttpStatus.OK);
    } catch (JsonProcessingException exception) {
      log.error(
          "JSON processing error while searching for artist '{}': {}",
          artistName,
          exception.getMessage(),
          exception);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (WebClientResponseException exception) {
      log.error(
          "iTunes API error while searching for artist '{}': HTTP {} - {}",
          artistName,
          exception.getStatusCode(),
          exception.getResponseBodyAsString(),
          exception);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (Exception exception) {
      log.error(
          "Unexpected error while searching for artist '{}': {}",
          artistName,
          exception.getMessage(),
          exception);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Makes a request to iTunes for all artists with a name that closely match the provided
   * artistName using WebClient.
   *
   * @param artistName The to-be-queried name.
   * @return An ArtistResponse object containing a list of Artists from iTunes.
   * @throws JsonProcessingException If there are any errors processing the response from iTunes.
   */
  private ArtistResponse fetchArtistsFromItunes(String artistName)
      throws JsonProcessingException {
    final String fullSearchUrl = String.format(iTunesSearch, artistName);
    log.debug("Making iTunes API search request to: {}", fullSearchUrl);

    String jsonQueryResult =
        webClient
            .get()
            .uri(fullSearchUrl)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(10))
            .doOnError(
                error ->
                    log.error(
                        "WebClient error while searching for artist '{}': {}",
                        artistName,
                        error.getMessage()))
            .onErrorResume(
                error -> {
                  log.warn("Retrying iTunes API search for artist: '{}'", artistName);
                  return Mono.error(error);
                })
            .block();

    if (log.isDebugEnabled()) {
      log.debug(
          "iTunes API search response for '{}': {}",
          artistName,
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));
    }

    artistResponse = objectMapper.readValue(jsonQueryResult, ArtistResponse.class);
    log.debug("Found {} artists matching '{}'", artistResponse.getResultCount(), artistName);

    return artistResponse;
  }
}
