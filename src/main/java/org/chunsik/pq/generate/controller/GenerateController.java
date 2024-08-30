package org.chunsik.pq.generate.controller;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.*;
import org.chunsik.pq.generate.service.GenerateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class GenerateController {
    private final GenerateService generateService;

    @PostMapping("/image")
    public GenerateResponseDTO generateImage(@RequestBody GenerateImageDTO generateImageDTO) throws IOException {
        return generateService.generateImage(generateImageDTO);
    }

    @PostMapping("/ticket")
    public CreateImageResponseDto createImage(@ModelAttribute GenerateApiRequestDTO dto) throws IOException {
        return generateService.createImage(dto);
    }

    @GetMapping("/ticket/{id}")
    public TicketResponseDTO getTicket(@PathVariable Long id) {
        return generateService.findTicketById(id);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException e) {
        Sentry.captureException(e);
        if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
            return new ResponseEntity<>("Too Many Request", e.getStatusCode());
        } else {
            return new ResponseEntity<>("Generate Reject", e.getStatusCode());
        }
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(HttpServerErrorException e) {
        Sentry.captureException(e);
        return new ResponseEntity<>("Server Internal Error", e.getStatusCode());
    }
}