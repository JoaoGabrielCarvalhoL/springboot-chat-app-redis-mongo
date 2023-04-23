package br.com.carv.websocket.chat.provider.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JsonWebTokenProviderTest {

    Logger logger = Logger.getLogger(JsonWebTokenProviderTest.class.getCanonicalName());

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    public void should_return_exception_with_invalid_token() {
        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> tokenProvider.decode("invalid token"));
    }



}