package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FreelancerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Freelancer getFreelancerSample1() {
        return new Freelancer().id(1L).name("name1").contact("contact1").qualification("qualification1");
    }

    public static Freelancer getFreelancerSample2() {
        return new Freelancer().id(2L).name("name2").contact("contact2").qualification("qualification2");
    }

    public static Freelancer getFreelancerRandomSampleGenerator() {
        return new Freelancer()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .contact(UUID.randomUUID().toString())
            .qualification(UUID.randomUUID().toString());
    }
}
