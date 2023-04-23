package br.com.carv.websocket.chat.service;

import java.util.Optional;

public interface TicketService {

    String buildAndSaveTicket(String token);

    Optional<String> getUserIdByTicket(String ticket);
}
