package br.com.carv.websocket.chat.handler;

public record MessagePayload(String to, String text) {
}
