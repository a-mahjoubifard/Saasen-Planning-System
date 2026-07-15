package com.saasen.nl.domain;

import static com.saasen.nl.domain.AssignedResourceTestSamples.*;
import static com.saasen.nl.domain.ResourceAvailabilityTestSamples.*;
import static com.saasen.nl.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignedResourceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignedResource.class);
        AssignedResource assignedResource1 = getAssignedResourceSample1();
        AssignedResource assignedResource2 = new AssignedResource();
        assertThat(assignedResource1).isNotEqualTo(assignedResource2);

        assignedResource2.setId(assignedResource1.getId());
        assertThat(assignedResource1).isEqualTo(assignedResource2);

        assignedResource2 = getAssignedResourceSample2();
        assertThat(assignedResource1).isNotEqualTo(assignedResource2);
    }

    @Test
    void resourceAvailabilityTest() {
        AssignedResource assignedResource = getAssignedResourceRandomSampleGenerator();
        ResourceAvailability resourceAvailabilityBack = getResourceAvailabilityRandomSampleGenerator();

        assignedResource.setResourceAvailability(resourceAvailabilityBack);
        assertThat(assignedResource.getResourceAvailability()).isEqualTo(resourceAvailabilityBack);

        assignedResource.resourceAvailability(null);
        assertThat(assignedResource.getResourceAvailability()).isNull();
    }

    @Test
    void sessionTest() {
        AssignedResource assignedResource = getAssignedResourceRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        assignedResource.setSession(sessionBack);
        assertThat(assignedResource.getSession()).isEqualTo(sessionBack);

        assignedResource.session(null);
        assertThat(assignedResource.getSession()).isNull();
    }
}
