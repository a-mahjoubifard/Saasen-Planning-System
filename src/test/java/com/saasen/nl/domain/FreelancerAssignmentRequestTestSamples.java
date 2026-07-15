package com.saasen.nl.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FreelancerAssignmentRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static FreelancerAssignmentRequest getFreelancerAssignmentRequestSample1() {
        return new FreelancerAssignmentRequest().id(1L);
    }

    public static FreelancerAssignmentRequest getFreelancerAssignmentRequestSample2() {
        return new FreelancerAssignmentRequest().id(2L);
    }

    public static FreelancerAssignmentRequest getFreelancerAssignmentRequestRandomSampleGenerator() {
        return new FreelancerAssignmentRequest().id(longCount.incrementAndGet());
    }
}
