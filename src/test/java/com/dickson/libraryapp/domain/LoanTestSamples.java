package com.dickson.libraryapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Loan getLoanSample1() {
        return new Loan().id(1L).renewals(1).notes("notes1");
    }

    public static Loan getLoanSample2() {
        return new Loan().id(2L).renewals(2).notes("notes2");
    }

    public static Loan getLoanRandomSampleGenerator() {
        return new Loan().id(longCount.incrementAndGet()).renewals(intCount.incrementAndGet()).notes(UUID.randomUUID().toString());
    }
}
