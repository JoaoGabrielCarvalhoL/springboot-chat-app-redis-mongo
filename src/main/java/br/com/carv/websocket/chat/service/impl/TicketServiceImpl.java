package br.com.carv.websocket.chat.service.impl;

import br.com.carv.websocket.chat.exception.TokenException;
import br.com.carv.websocket.chat.model.User;
import br.com.carv.websocket.chat.provider.impl.TokenProvider;
import br.com.carv.websocket.chat.repository.UserRepository;
import br.com.carv.websocket.chat.service.TicketService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class TicketServiceImpl implements TicketService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(TicketServiceImpl.class.getCanonicalName());

    public TicketServiceImpl(RedisTemplate<String, String> redisTemplate, TokenProvider tokenProvider,
                             UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }


    @Override
    public String buildAndSaveTicket(String token) {
        logger.info("Getting a Ticket'");
        if (token == null || token.isBlank()) {
            throw new TokenException("Invalid Token.");
        }

        String ticket = UUID.randomUUID().toString();
        Map<String, String> user = tokenProvider.decode(token);
        String userId = user.get("id");
        redisTemplate.opsForValue().set(ticket, userId, Duration.ofSeconds(60L));
        saveUser(user);
        return ticket;
    }

    @Override
    public Optional<String> getUserIdByTicket(String ticket) {
        return Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(ticket));
    }

    private void saveUser(Map<String, String> user) {
        userRepository.save(new User(user.get("id"), user.get("name"), user.get("picture")));
    }
}
