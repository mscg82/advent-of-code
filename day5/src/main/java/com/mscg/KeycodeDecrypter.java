package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeycodeDecrypter {
    
    private final String doorId;

    public String findPassword1() {
        var generator = new AtomicLong(0);
        var results = new ConcurrentHashMap<Long, Character>();
        int maxThreads = 6;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        Runnable r = () -> {
            while (!Thread.interrupted()) {
                long l = generator.getAndIncrement();
                String hash = DigestUtils.md5Hex(doorId + l);
                if ("00000".equals(hash.substring(0, 5))) {
                    results.put(l, hash.charAt(5));
                }
                if (results.size() >= 8) {
                    break;
                }
            }
        };
        for (int i = 0; i < maxThreads; i++) {
            executor.submit(r);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results.entrySet().stream() //
                .sorted(Comparator.comparing(Entry::getKey)) //
                .map(e -> e.getValue().toString()) //
                .collect(Collectors.joining());
    }

    public String findPassword2() {
        var generator = new AtomicLong(0);
        var results = new ConcurrentHashMap<Integer, Character>();
        int maxThreads = 6;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        Runnable r = () -> {
            while (!Thread.interrupted()) {
                long l = generator.getAndIncrement();
                String hash = DigestUtils.md5Hex(doorId + l);
                if ("00000".equals(hash.substring(0, 5))) {
                int position = switch (hash.charAt(5)) {
                    case '0' -> 0;
                    case '1' -> 1;
                    case '2' -> 2;
                    case '3' -> 3;
                    case '4' -> 4;
                    case '5' -> 5;
                    case '6' -> 6;
                    case '7' -> 7;
                    default -> -1;
                };
                if (position == -1) {
                    continue;
                }
                results.computeIfAbsent(position, __ -> hash.charAt(6));
                }
                if (results.size() >= 8) {
                    break;
                }
            }
        };
        for (int i = 0; i < maxThreads; i++) {
            executor.submit(r);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results.entrySet().stream() //
                .sorted(Comparator.comparing(Entry::getKey)) //
                .map(e -> e.getValue().toString()) //
                .collect(Collectors.joining());
    }

    public static KeycodeDecrypter parseInput(BufferedReader in) throws IOException {
        return new KeycodeDecrypter(in.readLine());
    }
}
