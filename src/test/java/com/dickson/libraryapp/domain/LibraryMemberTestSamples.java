package com.dickson.libraryapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LibraryMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LibraryMember getLibraryMemberSample1() {
        return new LibraryMember().id(1L).membershipNumber(1L).phoneNumber("phoneNumber1").address("address1");
    }

    public static LibraryMember getLibraryMemberSample2() {
        return new LibraryMember().id(2L).membershipNumber(2L).phoneNumber("phoneNumber2").address("address2");
    }

    public static LibraryMember getLibraryMemberRandomSampleGenerator() {
        return new LibraryMember()
            .id(longCount.incrementAndGet())
            .membershipNumber(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
