package com.solo.learning.tdourado.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solo.learning.tdourado.api.model.AlbumResponse;
import com.solo.learning.tdourado.api.model.ArtistResponse;
import com.solo.learning.tdourado.api.service.AlbumService;
import com.solo.learning.tdourado.api.service.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

/**
 * Controller for the lookups and searches using the iTunes API.
 *
 * @author tiberiusdourado
 */
@Slf4j
@RestController
@RequestMapping("/artist")
public class TunesController {

  ArtistService artistService;
  AlbumService albumService;

  public TunesController(ArtistService artistService, AlbumService albumService) {
    this.artistService = artistService;
    this.albumService = albumService;
  }

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
    AlbumResponse albumResponse;
    try {
      albumResponse = albumService.fetchAlbumsFromItunes(artistId);
    } catch (JsonProcessingException | RestClientException exception) {
      log.debug("Error while processing the Album request: {}", exception.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(albumResponse, HttpStatus.OK);
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
    ArtistResponse artistResponse;
    try {
      artistResponse = artistService.fetchArtistsFromItunes(artistName);
    } catch (JsonProcessingException | RestClientException exception) {
      log.debug("Error while processing the Artist request: {}", exception.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(artistResponse, HttpStatus.OK);
  }
}
