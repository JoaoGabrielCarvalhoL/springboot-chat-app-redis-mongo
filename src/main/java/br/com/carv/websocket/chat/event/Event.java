package br.com.carv.websocket.chat.event;

public record Event<T>(EventType type, T payload) {
}
