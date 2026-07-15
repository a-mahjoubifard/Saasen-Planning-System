<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="instructor">
        <h2 class="jh-entity-heading" data-cy="instructorDetailsHeading"><span>InstructorInstructor</span> {{ instructor.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ instructor.name }}</span>
          </dd>
          <dt>
            <span>Contact</span>
          </dt>
          <dd>
            <span>{{ instructor.contact }}</span>
          </dd>
          <dt>
            <span>Has Car</span>
          </dt>
          <dd>
            <span>{{ instructor.hasCar }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="instructor.id"
          :to="{ name: 'InstructorEdit', params: { instructorId: instructor.id } }"
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

<script lang="ts" src="./instructor-details.component.ts"></script>
