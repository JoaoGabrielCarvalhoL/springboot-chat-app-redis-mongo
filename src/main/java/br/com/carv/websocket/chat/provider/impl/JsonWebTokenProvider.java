package br.com.carv.websocket.chat.provider.impl;

import br.com.carv.websocket.chat.provider.KeyProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Service
public class JsonWebTokenProvider implements TokenProvider {

    private KeyProvider keyProvider;

    public JsonWebTokenProvider(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public Map<String, String> decode(String token) {
        DecodedJWT jwt = JWT.decode(token);
        PublicKey publicKey = keyProvider.getPublicKey(jwt.getKeyId());
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
        algorithm.verify(jwt);

        boolean expired = jwt.getExpiresAtAsInstant().atZone(ZoneId.systemDefault())
                .isBefore(ZonedDateTime.now());

        if(expired) throw new RuntimeException("token is expired.");

        return Map.of(
                "id", jwt.getSubject(),
                "name", jwt.getClaim("name").asString(),
                "picture", jwt.getClaim("picture").asString()
        );
    }
}
