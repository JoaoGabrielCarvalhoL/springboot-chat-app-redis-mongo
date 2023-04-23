package br.com.carv.websocket.chat.provider;

import java.security.PublicKey;

public interface KeyProvider {

    PublicKey getPublicKey(String keyId);
}
