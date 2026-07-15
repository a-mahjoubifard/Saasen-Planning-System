package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LocationAvailabilityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LocationAvailability getLocationAvailabilitySample1() {
        return new LocationAvailability().id(1L).title("title1");
    }

    public static LocationAvailability getLocationAvailabilitySample2() {
        return new LocationAvailability().id(2L).title("title2");
    }

    public static LocationAvailability getLocationAvailabilityRandomSampleGenerator() {
        return new LocationAvailability().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
