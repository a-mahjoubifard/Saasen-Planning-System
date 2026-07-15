package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrainingRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TrainingRequest getTrainingRequestSample1() {
        return new TrainingRequest().id(1L).description("description1");
    }

    public static TrainingRequest getTrainingRequestSample2() {
        return new TrainingRequest().id(2L).description("description2");
    }

    public static TrainingRequest getTrainingRequestRandomSampleGenerator() {
        return new TrainingRequest().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
