package com.saasen.nl.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ResourceAvailabilityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ResourceAvailability getResourceAvailabilitySample1() {
        return new ResourceAvailability().id(1L);
    }

    public static ResourceAvailability getResourceAvailabilitySample2() {
        return new ResourceAvailability().id(2L);
    }

    public static ResourceAvailability getResourceAvailabilityRandomSampleGenerator() {
        return new ResourceAvailability().id(longCount.incrementAndGet());
    }
}
