<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="instructorAvailability">
        <h2 class="jh-entity-heading" data-cy="instructorAvailabilityDetailsHeading">
          <span>Instructor AvailabilityInstructorAvailability</span> {{ instructorAvailability.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ instructorAvailability.title }}</span>
          </dd>
          <dt>
            <span>Available From</span>
          </dt>
          <dd>
            <span v-if="instructorAvailability.availableFrom">{{ formatDateLong(instructorAvailability.availableFrom) }}</span>
          </dd>
          <dt>
            <span>Available To</span>
          </dt>
          <dd>
            <span v-if="instructorAvailability.availableTo">{{ formatDateLong(instructorAvailability.availableTo) }}</span>
          </dd>
          <dt>
            <span>Instructor</span>
          </dt>
          <dd>
            <div v-if="instructorAvailability.instructor">
              <router-link :to="{ name: 'InstructorView', params: { instructorId: instructorAvailability.instructor.id } }">{{
                instructorAvailability.instructor.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="instructorAvailability.id"
          :to="{ name: 'InstructorAvailabilityEdit', params: { instructorAvailabilityId: instructorAvailability.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./instructor-availability-details.component.ts"></script>
