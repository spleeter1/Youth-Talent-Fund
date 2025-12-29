package com.graduation.youthtalentfund.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketTokenStore {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public void put(String token, String donationCode) {
        store.put(token, donationCode);
    }

    public String getDonationCode(String token) {
        return store.get(token);
    }

    public void remove(String token) {
        store.remove(token);
    }
}

