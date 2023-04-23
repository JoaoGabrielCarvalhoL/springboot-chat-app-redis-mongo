package br.com.carv.websocket.chat.handler;

import br.com.carv.websocket.chat.dto.ChatMessage;
import br.com.carv.websocket.chat.event.Event;
import br.com.carv.websocket.chat.event.EventType;
import br.com.carv.websocket.chat.model.User;
import br.com.carv.websocket.chat.pubsub.PublisherImpl;
import br.com.carv.websocket.chat.service.TicketService;
import br.com.carv.websocket.chat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = Logger.getLogger(WebSocketHandler.class.getCanonicalName());
    private final TicketService ticketService;
    private final Map<String, WebSocketSession> sessions;
    private final UserService userService;
    private final PublisherImpl publisher;
    private final Map<String, String> userIds;

    public WebSocketHandler(TicketService ticketService, UserService userService, PublisherImpl publisher) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.publisher = publisher;
        this.sessions = new ConcurrentHashMap<>();
        this.userIds = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("[afterConnectionEstablished] Session id: " + session.getId());
        Optional<String> ticket = ticketOf(session);

        if (ticket.isEmpty() || ticket.get().isBlank()) {
            logger.warning("Session " + session.getId() + " without ticket.");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        Optional<String> userId = this.ticketService.getUserIdByTicket(ticket.get());

        if (userId.isEmpty()) {
            logger.warning("Session " + session.getId() + " with invalid ticket.");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        sessions.put(userId.get(), session);
        userIds.put(session.getId(), userId.get());
        logger.warning("Session " + session.getId() + " was bind to user " + userId.get());
        sendChatUsers(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("[handleTextMessage] Message: " + message.getPayload());
        if(message.getPayload().equals("ping")) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }
        MessagePayload payload = new ObjectMapper().readValue(message.getPayload(), MessagePayload.class);
        String userIdFrom = userIds.get(session.getId());
        publisher.publishChatMessage(userIdFrom, payload.to(), payload.text());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("[afterConnectionClosed] Session id: " + session.getId());
        String userId = userIds.get(session.getId());
        if (userId != null) {
            sessions.remove(userId);
        }
        userIds.remove(session.getId());
    }


    private Optional<String> ticketOf(WebSocketSession session) {
        return Optional.ofNullable(session.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(value -> value.get("ticket"))
                .flatMap(value -> value.stream().findFirst())
                .map(String::trim);
    }

    private void sendChatUsers(WebSocketSession session) {
        List<User> chatUsers = this.userService.findChatUsers();
        Event<List<User>> event = new Event<>(EventType.CHAT_USERS_WERE_UPDATED, chatUsers);
        sendEvent(session, event);
    }

    public void notify(ChatMessage chatMessage) {
        Event<ChatMessage> event = new Event<>(EventType.CHAT_MESSAGE_WAS_CREATED, chatMessage);
        List<String> userIds = List.of(chatMessage.from().id(), chatMessage.to().id());
        userIds.stream()
                .distinct().map(sessions::get)
                .filter(Objects::nonNull)
                .forEach(session -> sendEvent(session, event));
        logger.info("Chat Message was notified.");
    }

    private void sendEvent(WebSocketSession session, Event<?> event) {
        try {
            String eventSerialized = new ObjectMapper().writeValueAsString(event);
            session.sendMessage(new TextMessage(eventSerialized));
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
