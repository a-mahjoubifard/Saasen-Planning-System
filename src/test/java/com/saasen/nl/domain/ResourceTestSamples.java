package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ResourceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Resource getResourceSample1() {
        return new Resource().id(1L).name("name1").resourceType("resourceType1").status("status1");
    }

    public static Resource getResourceSample2() {
        return new Resource().id(2L).name("name2").resourceType("resourceType2").status("status2");
    }

    public static Resource getResourceRandomSampleGenerator() {
        return new Resource()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .resourceType(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
