package com.graduation.youthtalentfund.websocket;

import com.graduation.youthtalentfund.session.WebSocketTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class WsListener {
    @Autowired
    private WebSocketTokenStore wsTokenStore;

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) return;

        String wsToken = (String) attrs.get("WS_TOKEN");
        if (wsToken != null) {
            wsTokenStore.remove(wsToken);
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) return;

        String wsToken = (String) attrs.get("WS_TOKEN");
        if (wsToken != null) {
            wsTokenStore.remove(wsToken);
        }
    }
}
