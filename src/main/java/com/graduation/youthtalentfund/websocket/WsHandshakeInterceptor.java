package com.graduation.youthtalentfund.websocket;

import com.graduation.youthtalentfund.session.WebSocketTokenStore;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private WebSocketTokenStore wsTokenStore;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        String wsToken = getParam(request, "wsToken");

        String donationCode = wsTokenStore.getDonationCode(wsToken);
        if (donationCode == null) {
            return false; // reject WS connection
        }

        attributes.put("DONATION_CODE", donationCode);
        attributes.put("WS_TOKEN", wsToken);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String getParam(ServerHttpRequest request, String key) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(key);
    }
}
