package br.com.carv.websocket.chat.repository;

import br.com.carv.websocket.chat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
