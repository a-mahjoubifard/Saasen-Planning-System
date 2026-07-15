package com.saasen.nl.domain;

import static com.saasen.nl.domain.FreelancerTestSamples.*;
import static com.saasen.nl.domain.InstructorAvailabilityTestSamples.*;
import static com.saasen.nl.domain.LocationAvailabilityTestSamples.*;
import static com.saasen.nl.domain.RequestedCourseTestSamples.*;
import static com.saasen.nl.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Session.class);
        Session session1 = getSessionSample1();
        Session session2 = new Session();
        assertThat(session1).isNotEqualTo(session2);

        session2.setId(session1.getId());
        assertThat(session1).isEqualTo(session2);

        session2 = getSessionSample2();
        assertThat(session1).isNotEqualTo(session2);
    }

    @Test
    void instructorAvailabilityTest() {
        Session session = getSessionRandomSampleGenerator();
        InstructorAvailability instructorAvailabilityBack = getInstructorAvailabilityRandomSampleGenerator();

        session.setInstructorAvailability(instructorAvailabilityBack);
        assertThat(session.getInstructorAvailability()).isEqualTo(instructorAvailabilityBack);

        session.instructorAvailability(null);
        assertThat(session.getInstructorAvailability()).isNull();
    }

    @Test
    void locationAvailabilityTest() {
        Session session = getSessionRandomSampleGenerator();
        LocationAvailability locationAvailabilityBack = getLocationAvailabilityRandomSampleGenerator();

        session.setLocationAvailability(locationAvailabilityBack);
        assertThat(session.getLocationAvailability()).isEqualTo(locationAvailabilityBack);

        session.locationAvailability(null);
        assertThat(session.getLocationAvailability()).isNull();
    }

    @Test
    void requestedCourseTest() {
        Session session = getSessionRandomSampleGenerator();
        RequestedCourse requestedCourseBack = getRequestedCourseRandomSampleGenerator();

        session.setRequestedCourse(requestedCourseBack);
        assertThat(session.getRequestedCourse()).isEqualTo(requestedCourseBack);

        session.requestedCourse(null);
        assertThat(session.getRequestedCourse()).isNull();
    }

    @Test
    void freelancerTest() {
        Session session = getSessionRandomSampleGenerator();
        Freelancer freelancerBack = getFreelancerRandomSampleGenerator();

        session.setFreelancer(freelancerBack);
        assertThat(session.getFreelancer()).isEqualTo(freelancerBack);

        session.freelancer(null);
        assertThat(session.getFreelancer()).isNull();
    }
}
