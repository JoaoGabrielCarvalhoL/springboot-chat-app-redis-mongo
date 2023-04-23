package br.com.carv.websocket.chat.pubsub;

import br.com.carv.websocket.chat.config.RedisConfig;
import br.com.carv.websocket.chat.dto.ChatMessage;
import br.com.carv.websocket.chat.model.User;
import br.com.carv.websocket.chat.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PublisherImpl {

    private final Logger logger = Logger.getLogger(PublisherImpl.class.getCanonicalName());
    private final UserRepository userRepository;
    private final ReactiveStringRedisTemplate redisTemplate;

    public PublisherImpl(UserRepository userRepository, ReactiveStringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public void publishChatMessage(String userIdFrom, String userIdTo, String text) throws JsonProcessingException {
        User from = this.userRepository.findById(userIdFrom).orElseThrow();
        User to = this.userRepository.findById(userIdTo).orElseThrow();
        ChatMessage chatMessage = new ChatMessage(from, to, text);
        String chatMessageSerialized = new ObjectMapper().writeValueAsString(chatMessage);
        redisTemplate.convertAndSend(RedisConfig.CHAT_MESSAGES_CHANEL, chatMessageSerialized).subscribe();
        logger.info("Chat Message was published.");
    }
}
