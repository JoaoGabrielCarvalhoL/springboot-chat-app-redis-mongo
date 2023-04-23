package br.com.carv.websocket.chat.service;

import br.com.carv.websocket.chat.model.User;

import java.util.List;

public interface UserService {

    List<User> findChatUsers();
}
