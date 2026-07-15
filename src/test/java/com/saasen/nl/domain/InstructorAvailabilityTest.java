package com.saasen.nl.domain;

import static com.saasen.nl.domain.InstructorAvailabilityTestSamples.*;
import static com.saasen.nl.domain.InstructorTestSamples.*;
import static com.saasen.nl.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstructorAvailabilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstructorAvailability.class);
        InstructorAvailability instructorAvailability1 = getInstructorAvailabilitySample1();
        InstructorAvailability instructorAvailability2 = new InstructorAvailability();
        assertThat(instructorAvailability1).isNotEqualTo(instructorAvailability2);

        instructorAvailability2.setId(instructorAvailability1.getId());
        assertThat(instructorAvailability1).isEqualTo(instructorAvailability2);

        instructorAvailability2 = getInstructorAvailabilitySample2();
        assertThat(instructorAvailability1).isNotEqualTo(instructorAvailability2);
    }

    @Test
    void instructorTest() {
        InstructorAvailability instructorAvailability = getInstructorAvailabilityRandomSampleGenerator();
        Instructor instructorBack = getInstructorRandomSampleGenerator();

        instructorAvailability.setInstructor(instructorBack);
        assertThat(instructorAvailability.getInstructor()).isEqualTo(instructorBack);

        instructorAvailability.instructor(null);
        assertThat(instructorAvailability.getInstructor()).isNull();
    }

    @Test
    void sessionTest() {
        InstructorAvailability instructorAvailability = getInstructorAvailabilityRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        instructorAvailability.setSession(sessionBack);
        assertThat(instructorAvailability.getSession()).isEqualTo(sessionBack);
        assertThat(sessionBack.getInstructorAvailability()).isEqualTo(instructorAvailability);

        instructorAvailability.session(null);
        assertThat(instructorAvailability.getSession()).isNull();
        assertThat(sessionBack.getInstructorAvailability()).isNull();
    }
}
