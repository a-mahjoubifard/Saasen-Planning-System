package com.saasen.nl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RequestedCourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RequestedCourse getRequestedCourseSample1() {
        return new RequestedCourse().id(1L).numberOfParticipants(1).preferredLocation("preferredLocation1");
    }

    public static RequestedCourse getRequestedCourseSample2() {
        return new RequestedCourse().id(2L).numberOfParticipants(2).preferredLocation("preferredLocation2");
    }

    public static RequestedCourse getRequestedCourseRandomSampleGenerator() {
        return new RequestedCourse()
            .id(longCount.incrementAndGet())
            .numberOfParticipants(intCount.incrementAndGet())
            .preferredLocation(UUID.randomUUID().toString());
    }
}
