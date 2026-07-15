package com.dickson.libraryapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BookCopyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static BookCopy getBookCopySample1() {
        return new BookCopy().id(1L).barcode("barcode1").shelfLocation("shelfLocation1");
    }

    public static BookCopy getBookCopySample2() {
        return new BookCopy().id(2L).barcode("barcode2").shelfLocation("shelfLocation2");
    }

    public static BookCopy getBookCopyRandomSampleGenerator() {
        return new BookCopy()
            .id(longCount.incrementAndGet())
            .barcode(UUID.randomUUID().toString())
            .shelfLocation(UUID.randomUUID().toString());
    }
}
