package br.com.carv.websocket.chat.provider.impl;

import java.util.Map;

public interface TokenProvider {

    Map<String, String> decode(String token);
}
