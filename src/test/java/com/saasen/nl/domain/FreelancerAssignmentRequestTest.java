package com.saasen.nl.domain;

import static com.saasen.nl.domain.FreelancerAssignmentRequestTestSamples.*;
import static com.saasen.nl.domain.FreelancerTestSamples.*;
import static com.saasen.nl.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FreelancerAssignmentRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FreelancerAssignmentRequest.class);
        FreelancerAssignmentRequest freelancerAssignmentRequest1 = getFreelancerAssignmentRequestSample1();
        FreelancerAssignmentRequest freelancerAssignmentRequest2 = new FreelancerAssignmentRequest();
        assertThat(freelancerAssignmentRequest1).isNotEqualTo(freelancerAssignmentRequest2);

        freelancerAssignmentRequest2.setId(freelancerAssignmentRequest1.getId());
        assertThat(freelancerAssignmentRequest1).isEqualTo(freelancerAssignmentRequest2);

        freelancerAssignmentRequest2 = getFreelancerAssignmentRequestSample2();
        assertThat(freelancerAssignmentRequest1).isNotEqualTo(freelancerAssignmentRequest2);
    }

    @Test
    void freelancerTest() {
        FreelancerAssignmentRequest freelancerAssignmentRequest = getFreelancerAssignmentRequestRandomSampleGenerator();
        Freelancer freelancerBack = getFreelancerRandomSampleGenerator();

        freelancerAssignmentRequest.setFreelancer(freelancerBack);
        assertThat(freelancerAssignmentRequest.getFreelancer()).isEqualTo(freelancerBack);

        freelancerAssignmentRequest.freelancer(null);
        assertThat(freelancerAssignmentRequest.getFreelancer()).isNull();
    }

    @Test
    void sessionTest() {
        FreelancerAssignmentRequest freelancerAssignmentRequest = getFreelancerAssignmentRequestRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        freelancerAssignmentRequest.setSession(sessionBack);
        assertThat(freelancerAssignmentRequest.getSession()).isEqualTo(sessionBack);

        freelancerAssignmentRequest.session(null);
        assertThat(freelancerAssignmentRequest.getSession()).isNull();
    }
}
