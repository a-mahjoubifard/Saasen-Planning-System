<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="course">
        <h2 class="jh-entity-heading" data-cy="courseDetailsHeading"><span>CourseCourse</span> {{ course.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ course.title }}</span>
          </dd>
          <dt>
            <span>Duration</span>
          </dt>
          <dd>
            <span>{{ course.duration }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="course.id" :to="{ name: 'CourseEdit', params: { courseId: course.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./course-details.component.ts"></script>
