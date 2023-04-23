package br.com.carv.websocket.chat.provider.impl;

import br.com.carv.websocket.chat.provider.KeyProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

@Service
public class JsonWebKeyProvider implements KeyProvider {

    private final UrlJwkProvider urlJwkProvider;

    public JsonWebKeyProvider(@Value("${app.auth.jwks-url}") final String jwksUrl) {
        try {
            this.urlJwkProvider = new UrlJwkProvider(new URL(jwksUrl));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable("public-key")
    @Override
    public PublicKey getPublicKey(String keyId) {
        try {
            final Jwk jwk = urlJwkProvider.get(keyId);
            return jwk.getPublicKey();
        } catch (JwkException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
