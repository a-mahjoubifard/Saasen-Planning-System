package com.saasen.nl.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AssignedResourceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static AssignedResource getAssignedResourceSample1() {
        return new AssignedResource().id(1L);
    }

    public static AssignedResource getAssignedResourceSample2() {
        return new AssignedResource().id(2L);
    }

    public static AssignedResource getAssignedResourceRandomSampleGenerator() {
        return new AssignedResource().id(longCount.incrementAndGet());
    }
}
