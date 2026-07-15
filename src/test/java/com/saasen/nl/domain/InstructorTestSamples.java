package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstructorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Instructor getInstructorSample1() {
        return new Instructor().id(1L).name("name1").contact("contact1");
    }

    public static Instructor getInstructorSample2() {
        return new Instructor().id(2L).name("name2").contact("contact2");
    }

    public static Instructor getInstructorRandomSampleGenerator() {
        return new Instructor().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).contact(UUID.randomUUID().toString());
    }
}
