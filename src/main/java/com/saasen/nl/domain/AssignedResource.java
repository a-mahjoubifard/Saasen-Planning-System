package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A AssignedResource.
 */
@Entity
@Table(name = "assigned_resource")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignedResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(value = { "resource", "assignedResource" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private ResourceAvailability resourceAvailability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "instructorAvailability", "locationAvailability", "requestedCourse", "freelancer" },
        allowSetters = true
    )
    private Session session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AssignedResource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceAvailability getResourceAvailability() {
        return this.resourceAvailability;
    }

    public void setResourceAvailability(ResourceAvailability resourceAvailability) {
        this.resourceAvailability = resourceAvailability;
    }

    public AssignedResource resourceAvailability(ResourceAvailability resourceAvailability) {
        this.setResourceAvailability(resourceAvailability);
        return this;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public AssignedResource session(Session session) {
        this.setSession(session);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignedResource)) {
            return false;
        }
        return getId() != null && getId().equals(((AssignedResource) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignedResource{" +
            "id=" + getId() +
            "}";
    }
}
