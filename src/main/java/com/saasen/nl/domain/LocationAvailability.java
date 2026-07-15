package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A LocationAvailability.
 */
@Entity
@Table(name = "location_availability")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationAvailability implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "available_from")
    private Instant availableFrom;

    @Column(name = "available_to")
    private Instant availableTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @JsonIgnoreProperties(
        value = { "instructorAvailability", "locationAvailability", "requestedCourse", "freelancer" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "locationAvailability")
    private Session session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LocationAvailability id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public LocationAvailability title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getAvailableFrom() {
        return this.availableFrom;
    }

    public LocationAvailability availableFrom(Instant availableFrom) {
        this.setAvailableFrom(availableFrom);
        return this;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableTo() {
        return this.availableTo;
    }

    public LocationAvailability availableTo(Instant availableTo) {
        this.setAvailableTo(availableTo);
        return this;
    }

    public void setAvailableTo(Instant availableTo) {
        this.availableTo = availableTo;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationAvailability location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        if (this.session != null) {
            this.session.setLocationAvailability(null);
        }
        if (session != null) {
            session.setLocationAvailability(this);
        }
        this.session = session;
    }

    public LocationAvailability session(Session session) {
        this.setSession(session);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationAvailability)) {
            return false;
        }
        return getId() != null && getId().equals(((LocationAvailability) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationAvailability{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableTo='" + getAvailableTo() + "'" +
            "}";
    }
}
