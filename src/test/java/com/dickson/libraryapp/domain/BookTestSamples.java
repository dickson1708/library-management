package com.dickson.libraryapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Book getBookSample1() {
        return new Book()
            .id(1L)
            .isbn("isbn1")
            .title("title1")
            .author("author1")
            .publisher("publisher1")
            .language("language1")
            .pages(1)
            .coverImage("coverImage1");
    }

    public static Book getBookSample2() {
        return new Book()
            .id(2L)
            .isbn("isbn2")
            .title("title2")
            .author("author2")
            .publisher("publisher2")
            .language("language2")
            .pages(2)
            .coverImage("coverImage2");
    }

    public static Book getBookRandomSampleGenerator() {
        return new Book()
            .id(longCount.incrementAndGet())
            .isbn(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .author(UUID.randomUUID().toString())
            .publisher(UUID.randomUUID().toString())
            .language(UUID.randomUUID().toString())
            .pages(intCount.incrementAndGet())
            .coverImage(UUID.randomUUID().toString());
    }
}
