package br.com.carv.websocket.chat.controller;

import br.com.carv.websocket.chat.service.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ticket")
@CrossOrigin
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> buildTicket(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = Optional.ofNullable(authorization)
                .map(value -> value.replace("Bearer ", ""))
                .orElse("");
        String ticket = ticketService.buildAndSaveTicket(token);
        return Map.of("ticket", ticket);
    }
}
