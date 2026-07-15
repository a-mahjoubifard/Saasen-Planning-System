package com.saasen.nl.domain;

import static com.saasen.nl.domain.InstructorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstructorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instructor.class);
        Instructor instructor1 = getInstructorSample1();
        Instructor instructor2 = new Instructor();
        assertThat(instructor1).isNotEqualTo(instructor2);

        instructor2.setId(instructor1.getId());
        assertThat(instructor1).isEqualTo(instructor2);

        instructor2 = getInstructorSample2();
        assertThat(instructor1).isNotEqualTo(instructor2);
    }
}
