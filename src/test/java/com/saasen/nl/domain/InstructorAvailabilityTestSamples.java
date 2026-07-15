package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstructorAvailabilityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static InstructorAvailability getInstructorAvailabilitySample1() {
        return new InstructorAvailability().id(1L).title("title1");
    }

    public static InstructorAvailability getInstructorAvailabilitySample2() {
        return new InstructorAvailability().id(2L).title("title2");
    }

    public static InstructorAvailability getInstructorAvailabilityRandomSampleGenerator() {
        return new InstructorAvailability().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
