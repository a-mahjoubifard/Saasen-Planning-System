package com.saasen.nl.domain;

import static com.saasen.nl.domain.CustomerTestSamples.*;
import static com.saasen.nl.domain.TrainingRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrainingRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrainingRequest.class);
        TrainingRequest trainingRequest1 = getTrainingRequestSample1();
        TrainingRequest trainingRequest2 = new TrainingRequest();
        assertThat(trainingRequest1).isNotEqualTo(trainingRequest2);

        trainingRequest2.setId(trainingRequest1.getId());
        assertThat(trainingRequest1).isEqualTo(trainingRequest2);

        trainingRequest2 = getTrainingRequestSample2();
        assertThat(trainingRequest1).isNotEqualTo(trainingRequest2);
    }

    @Test
    void customerTest() {
        TrainingRequest trainingRequest = getTrainingRequestRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        trainingRequest.setCustomer(customerBack);
        assertThat(trainingRequest.getCustomer()).isEqualTo(customerBack);

        trainingRequest.customer(null);
        assertThat(trainingRequest.getCustomer()).isNull();
    }
}
