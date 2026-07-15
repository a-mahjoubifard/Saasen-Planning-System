package com.saasen.nl.domain;

import static com.saasen.nl.domain.FreelancerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FreelancerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Freelancer.class);
        Freelancer freelancer1 = getFreelancerSample1();
        Freelancer freelancer2 = new Freelancer();
        assertThat(freelancer1).isNotEqualTo(freelancer2);

        freelancer2.setId(freelancer1.getId());
        assertThat(freelancer1).isEqualTo(freelancer2);

        freelancer2 = getFreelancerSample2();
        assertThat(freelancer1).isNotEqualTo(freelancer2);
    }
}
