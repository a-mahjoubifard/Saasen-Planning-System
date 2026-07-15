package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ResourceAvailability.
 */
@Entity
@Table(name = "resource_availability")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceAvailability implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "available_from")
    private Instant availableFrom;

    @Column(name = "available_to")
    private Instant availableTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resource resource;

    @JsonIgnoreProperties(value = { "resourceAvailability", "session" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "resourceAvailability")
    private AssignedResource assignedResource;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResourceAvailability id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAvailableFrom() {
        return this.availableFrom;
    }

    public ResourceAvailability availableFrom(Instant availableFrom) {
        this.setAvailableFrom(availableFrom);
        return this;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableTo() {
        return this.availableTo;
    }

    public ResourceAvailability availableTo(Instant availableTo) {
        this.setAvailableTo(availableTo);
        return this;
    }

    public void setAvailableTo(Instant availableTo) {
        this.availableTo = availableTo;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ResourceAvailability resource(Resource resource) {
        this.setResource(resource);
        return this;
    }

    public AssignedResource getAssignedResource() {
        return this.assignedResource;
    }

    public void setAssignedResource(AssignedResource assignedResource) {
        if (this.assignedResource != null) {
            this.assignedResource.setResourceAvailability(null);
        }
        if (assignedResource != null) {
            assignedResource.setResourceAvailability(this);
        }
        this.assignedResource = assignedResource;
    }

    public ResourceAvailability assignedResource(AssignedResource assignedResource) {
        this.setAssignedResource(assignedResource);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceAvailability)) {
            return false;
        }
        return getId() != null && getId().equals(((ResourceAvailability) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceAvailability{" +
            "id=" + getId() +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableTo='" + getAvailableTo() + "'" +
            "}";
    }
}
