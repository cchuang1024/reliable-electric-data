package edu.nccu.cs.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;

public class Randomizer {

    public static synchronized double randomDouble() {
        SecureRandom secRand = new SecureRandom();
        return secRand.nextDouble();
    }

    public static BigDecimal randomSign() {
        SecureRandom secRand = new SecureRandom();
        return secRand.nextBoolean() ? BigDecimal.valueOf(1.0) : BigDecimal.valueOf(-1.0);
    }
}
