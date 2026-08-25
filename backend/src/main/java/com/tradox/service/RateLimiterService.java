package com.tradox.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_SIZE_MILLIS = 60000; // 1 minute

    private final Map<String, Queue<Long>> requestWindowMap = new ConcurrentHashMap<>();

    public boolean allowRequest(String clientKey) {
        if (clientKey == null || clientKey.isBlank()) {
            clientKey = "ANONYMOUS";
        }

        long now = Instant.now().toEpochMilli();
        Queue<Long> timestamps = requestWindowMap.computeIfAbsent(clientKey, k -> new ConcurrentLinkedQueue<>());

        // Evict timestamps older than 1 minute
        while (!timestamps.isEmpty() && (now - timestamps.peek() > WINDOW_SIZE_MILLIS)) {
            timestamps.poll();
        }

        if (timestamps.size() < MAX_REQUESTS_PER_MINUTE) {
            timestamps.add(now);
            return true;
        }

        return false;
    }

    public int getRemainingRequests(String clientKey) {
        Queue<Long> timestamps = requestWindowMap.get(clientKey);
        if (timestamps == null) {
            return MAX_REQUESTS_PER_MINUTE;
        }
        return Math.max(0, MAX_REQUESTS_PER_MINUTE - timestamps.size());
    }
}
