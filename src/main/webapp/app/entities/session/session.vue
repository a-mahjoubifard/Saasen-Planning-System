<template>
  <div>
    <h2 id="page-heading" data-cy="SessionHeading">
      <span id="session">Sessions</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'SessionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-session"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Session</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && sessions?.length === 0">
      <span>No Sessions found</span>
    </div>
    <div class="table-responsive" v-if="sessions?.length > 0">
      <table class="table table-striped" aria-describedby="sessions">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Start Date</span></th>
            <th scope="col"><span>End Date</span></th>
            <th scope="col"><span>Status</span></th>
            <th scope="col"><span>Instructor Availability</span></th>
            <th scope="col"><span>Location Availability</span></th>
            <th scope="col"><span>Requested Course</span></th>
            <th scope="col"><span>Freelancer</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in sessions" :key="session.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'SessionView', params: { sessionId: session.id } }">{{ session.id }}</router-link>
            </td>
            <td>{{ formatDateShort(session.startDate) || '' }}</td>
            <td>{{ formatDateShort(session.endDate) || '' }}</td>
            <td>{{ session.status }}</td>
            <td>
              <div v-if="session.instructorAvailability">
                <router-link
                  :to="{ name: 'InstructorAvailabilityView', params: { instructorAvailabilityId: session.instructorAvailability.id } }"
                  >{{ session.instructorAvailability.title }}</router-link
                >
              </div>
            </td>
            <td>
              <div v-if="session.locationAvailability">
                <router-link
                  :to="{ name: 'LocationAvailabilityView', params: { locationAvailabilityId: session.locationAvailability.id } }"
                  >{{ session.locationAvailability.title }}</router-link
                >
              </div>
            </td>
            <td>
              <div v-if="session.requestedCourse">
                <router-link :to="{ name: 'RequestedCourseView', params: { requestedCourseId: session.requestedCourse.id } }">{{
                  session.requestedCourse.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="session.freelancer">
                <router-link :to="{ name: 'FreelancerView', params: { freelancerId: session.freelancer.id } }">{{
                  session.freelancer.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'SessionView', params: { sessionId: session.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'SessionEdit', params: { sessionId: session.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(session)"
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
        <span id="saasenApp.session.delete.question" data-cy="sessionDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-session-heading">Are you sure you want to delete Session {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-session"
            data-cy="entityConfirmDeleteButton"
            @click="removeSession"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./session.component.ts"></script>
