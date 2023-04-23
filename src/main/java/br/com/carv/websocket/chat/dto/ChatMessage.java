package br.com.carv.websocket.chat.dto;

import br.com.carv.websocket.chat.model.User;

public record ChatMessage(User from, User to, String text) {
}
