<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="requestedCourse">
        <h2 class="jh-entity-heading" data-cy="requestedCourseDetailsHeading">
          <span>Requested CourseRequestedCourse</span> {{ requestedCourse.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Number Of Participants</span>
          </dt>
          <dd>
            <span>{{ requestedCourse.numberOfParticipants }}</span>
          </dd>
          <dt>
            <span>Preferred Start Date</span>
          </dt>
          <dd>
            <span>{{ requestedCourse.preferredStartDate }}</span>
          </dd>
          <dt>
            <span>Preferred Location</span>
          </dt>
          <dd>
            <span>{{ requestedCourse.preferredLocation }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ requestedCourse.status }}</span>
          </dd>
          <dt>
            <span>Actual Start Date</span>
          </dt>
          <dd>
            <span v-if="requestedCourse.actualStartDate">{{ formatDateLong(requestedCourse.actualStartDate) }}</span>
          </dd>
          <dt>
            <span>Actual End Date</span>
          </dt>
          <dd>
            <span v-if="requestedCourse.actualEndDate">{{ formatDateLong(requestedCourse.actualEndDate) }}</span>
          </dd>
          <dt>
            <span>Training Request</span>
          </dt>
          <dd>
            <div v-if="requestedCourse.trainingRequest">
              <router-link :to="{ name: 'TrainingRequestView', params: { trainingRequestId: requestedCourse.trainingRequest.id } }">{{
                requestedCourse.trainingRequest.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Course</span>
          </dt>
          <dd>
            <div v-if="requestedCourse.course">
              <router-link :to="{ name: 'CourseView', params: { courseId: requestedCourse.course.id } }">{{
                requestedCourse.course.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="requestedCourse.id"
          :to="{ name: 'RequestedCourseEdit', params: { requestedCourseId: requestedCourse.id } }"
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

<script lang="ts" src="./requested-course-details.component.ts"></script>
