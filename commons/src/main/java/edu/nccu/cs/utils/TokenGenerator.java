package edu.nccu.cs.utils;

import java.util.UUID;

public class TokenGenerator {
    public static String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }
}
