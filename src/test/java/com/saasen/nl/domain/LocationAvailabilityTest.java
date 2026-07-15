package com.saasen.nl.domain;

import static com.saasen.nl.domain.LocationAvailabilityTestSamples.*;
import static com.saasen.nl.domain.LocationTestSamples.*;
import static com.saasen.nl.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationAvailabilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationAvailability.class);
        LocationAvailability locationAvailability1 = getLocationAvailabilitySample1();
        LocationAvailability locationAvailability2 = new LocationAvailability();
        assertThat(locationAvailability1).isNotEqualTo(locationAvailability2);

        locationAvailability2.setId(locationAvailability1.getId());
        assertThat(locationAvailability1).isEqualTo(locationAvailability2);

        locationAvailability2 = getLocationAvailabilitySample2();
        assertThat(locationAvailability1).isNotEqualTo(locationAvailability2);
    }

    @Test
    void locationTest() {
        LocationAvailability locationAvailability = getLocationAvailabilityRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        locationAvailability.setLocation(locationBack);
        assertThat(locationAvailability.getLocation()).isEqualTo(locationBack);

        locationAvailability.location(null);
        assertThat(locationAvailability.getLocation()).isNull();
    }

    @Test
    void sessionTest() {
        LocationAvailability locationAvailability = getLocationAvailabilityRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        locationAvailability.setSession(sessionBack);
        assertThat(locationAvailability.getSession()).isEqualTo(sessionBack);
        assertThat(sessionBack.getLocationAvailability()).isEqualTo(locationAvailability);

        locationAvailability.session(null);
        assertThat(locationAvailability.getSession()).isNull();
        assertThat(sessionBack.getLocationAvailability()).isNull();
    }
}
