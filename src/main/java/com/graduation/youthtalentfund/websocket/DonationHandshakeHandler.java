package com.graduation.youthtalentfund.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class DonationHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest request,
            @NotNull WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        return new DonationPrincipal(
                (String) attributes.get("DONATION_CODE")
        );
    }
}

