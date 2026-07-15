<template>
  <div>
    <h2 id="page-heading" data-cy="RequestedCourseHeading">
      <span id="requested-course">Requested Courses</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'RequestedCourseCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-requested-course"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Requested Course</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && requestedCourses?.length === 0">
      <span>No Requested Courses found</span>
    </div>
    <div class="table-responsive" v-if="requestedCourses?.length > 0">
      <table class="table table-striped" aria-describedby="requestedCourses">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Number Of Participants</span></th>
            <th scope="col"><span>Preferred Start Date</span></th>
            <th scope="col"><span>Preferred Location</span></th>
            <th scope="col"><span>Status</span></th>
            <th scope="col"><span>Actual Start Date</span></th>
            <th scope="col"><span>Actual End Date</span></th>
            <th scope="col"><span>Training Request</span></th>
            <th scope="col"><span>Course</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="requestedCourse in requestedCourses" :key="requestedCourse.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'RequestedCourseView', params: { requestedCourseId: requestedCourse.id } }">{{
                requestedCourse.id
              }}</router-link>
            </td>
            <td>{{ requestedCourse.numberOfParticipants }}</td>
            <td>{{ requestedCourse.preferredStartDate }}</td>
            <td>{{ requestedCourse.preferredLocation }}</td>
            <td>{{ requestedCourse.status }}</td>
            <td>{{ formatDateShort(requestedCourse.actualStartDate) || '' }}</td>
            <td>{{ formatDateShort(requestedCourse.actualEndDate) || '' }}</td>
            <td>
              <div v-if="requestedCourse.trainingRequest">
                <router-link :to="{ name: 'TrainingRequestView', params: { trainingRequestId: requestedCourse.trainingRequest.id } }">{{
                  requestedCourse.trainingRequest.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="requestedCourse.course">
                <router-link :to="{ name: 'CourseView', params: { courseId: requestedCourse.course.id } }">{{
                  requestedCourse.course.title
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'RequestedCourseView', params: { requestedCourseId: requestedCourse.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'RequestedCourseEdit', params: { requestedCourseId: requestedCourse.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(requestedCourse)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #title>
        <span id="saasenApp.requestedCourse.delete.question" data-cy="requestedCourseDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-requestedCourse-heading">Are you sure you want to delete Requested Course {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-requestedCourse"
            data-cy="entityConfirmDeleteButton"
            @click="removeRequestedCourse"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./requested-course.component.ts"></script>
