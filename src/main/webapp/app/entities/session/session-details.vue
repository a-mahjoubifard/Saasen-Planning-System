<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="session">
        <h2 class="jh-entity-heading" data-cy="sessionDetailsHeading"><span>SessionSession</span> {{ session.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Start Date</span>
          </dt>
          <dd>
            <span v-if="session.startDate">{{ formatDateLong(session.startDate) }}</span>
          </dd>
          <dt>
            <span>End Date</span>
          </dt>
          <dd>
            <span v-if="session.endDate">{{ formatDateLong(session.endDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ session.status }}</span>
          </dd>
          <dt>
            <span>Instructor Availability</span>
          </dt>
          <dd>
            <div v-if="session.instructorAvailability">
              <router-link
                :to="{ name: 'InstructorAvailabilityView', params: { instructorAvailabilityId: session.instructorAvailability.id } }"
                >{{ session.instructorAvailability.title }}</router-link
              >
            </div>
          </dd>
          <dt>
            <span>Location Availability</span>
          </dt>
          <dd>
            <div v-if="session.locationAvailability">
              <router-link
                :to="{ name: 'LocationAvailabilityView', params: { locationAvailabilityId: session.locationAvailability.id } }"
                >{{ session.locationAvailability.title }}</router-link
              >
            </div>
          </dd>
          <dt>
            <span>Requested Course</span>
          </dt>
          <dd>
            <div v-if="session.requestedCourse">
              <router-link :to="{ name: 'RequestedCourseView', params: { requestedCourseId: session.requestedCourse.id } }">{{
                session.requestedCourse.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Freelancer</span>
          </dt>
          <dd>
            <div v-if="session.freelancer">
              <router-link :to="{ name: 'FreelancerView', params: { freelancerId: session.freelancer.id } }">{{
                session.freelancer.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="session.id" :to="{ name: 'SessionEdit', params: { sessionId: session.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./session-details.component.ts"></script>
