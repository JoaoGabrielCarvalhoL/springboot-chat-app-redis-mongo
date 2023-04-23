package br.com.carv.websocket.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class HealthCheckController {

    private final Logger logger = Logger.getLogger(HealthCheckController.class.getCanonicalName());

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    void healthCheck() {
        logger.info("Health Check");
    }
}
