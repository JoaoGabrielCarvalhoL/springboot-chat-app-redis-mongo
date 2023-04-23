package br.com.carv.websocket.chat.pubsub;

import br.com.carv.websocket.chat.config.RedisConfig;
import br.com.carv.websocket.chat.dto.ChatMessage;
import br.com.carv.websocket.chat.handler.WebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Subscriber {

    private final Logger logger = Logger.getLogger(Subscriber.class.getCanonicalName());
    private final ReactiveStringRedisTemplate redisTemplate;
    private final WebSocketHandler webSocketHandler;

    public Subscriber(ReactiveStringRedisTemplate redisTemplate, WebSocketHandler webSocketHandler) {
        this.redisTemplate = redisTemplate;
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    private void init() {
        this.redisTemplate
                .listenTo(ChannelTopic.of(RedisConfig.CHAT_MESSAGES_CHANEL))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(this::onChatMessage);
    }

    private void onChatMessage(final String chatMessageSerialized) {
        logger.info("Chat Message was received.");
        try {
            ChatMessage chatMessage = new ObjectMapper().readValue(chatMessageSerialized, ChatMessage.class);
            webSocketHandler.notify(chatMessage);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
