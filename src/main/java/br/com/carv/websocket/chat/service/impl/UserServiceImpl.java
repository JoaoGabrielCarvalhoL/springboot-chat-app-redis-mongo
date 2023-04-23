package br.com.carv.websocket.chat.service.impl;

import br.com.carv.websocket.chat.model.User;
import br.com.carv.websocket.chat.repository.UserRepository;
import br.com.carv.websocket.chat.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getCanonicalName());
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findChatUsers() {
        logger.info("Getting all users");
        return this.userRepository.findAll();
    }
}
