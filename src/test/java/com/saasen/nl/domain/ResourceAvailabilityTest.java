package com.saasen.nl.domain;

import static com.saasen.nl.domain.AssignedResourceTestSamples.*;
import static com.saasen.nl.domain.ResourceAvailabilityTestSamples.*;
import static com.saasen.nl.domain.ResourceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceAvailabilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceAvailability.class);
        ResourceAvailability resourceAvailability1 = getResourceAvailabilitySample1();
        ResourceAvailability resourceAvailability2 = new ResourceAvailability();
        assertThat(resourceAvailability1).isNotEqualTo(resourceAvailability2);

        resourceAvailability2.setId(resourceAvailability1.getId());
        assertThat(resourceAvailability1).isEqualTo(resourceAvailability2);

        resourceAvailability2 = getResourceAvailabilitySample2();
        assertThat(resourceAvailability1).isNotEqualTo(resourceAvailability2);
    }

    @Test
    void resourceTest() {
        ResourceAvailability resourceAvailability = getResourceAvailabilityRandomSampleGenerator();
        Resource resourceBack = getResourceRandomSampleGenerator();

        resourceAvailability.setResource(resourceBack);
        assertThat(resourceAvailability.getResource()).isEqualTo(resourceBack);

        resourceAvailability.resource(null);
        assertThat(resourceAvailability.getResource()).isNull();
    }

    @Test
    void assignedResourceTest() {
        ResourceAvailability resourceAvailability = getResourceAvailabilityRandomSampleGenerator();
        AssignedResource assignedResourceBack = getAssignedResourceRandomSampleGenerator();

        resourceAvailability.setAssignedResource(assignedResourceBack);
        assertThat(resourceAvailability.getAssignedResource()).isEqualTo(assignedResourceBack);
        assertThat(assignedResourceBack.getResourceAvailability()).isEqualTo(resourceAvailability);

        resourceAvailability.assignedResource(null);
        assertThat(resourceAvailability.getAssignedResource()).isNull();
        assertThat(assignedResourceBack.getResourceAvailability()).isNull();
    }
}
