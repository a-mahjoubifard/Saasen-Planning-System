<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.session.home.createOrEditLabel" data-cy="SessionCreateUpdateHeading">Create or edit a Session</h2>
        <div>
          <div class="mb-3" v-if="session.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="session.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Start Date</label>
            <div class="d-flex">
              <input
                id="session-startDate"
                data-cy="startDate"
                type="datetime-local"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                :value="convertDateTimeFromServer(v$.startDate.$model)"
                @change="updateInstantField('startDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">End Date</label>
            <div class="d-flex">
              <input
                id="session-endDate"
                data-cy="endDate"
                type="datetime-local"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                :value="convertDateTimeFromServer(v$.endDate.$model)"
                @change="updateInstantField('endDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="session-status"
              data-cy="status"
            >
              <option v-for="requestStatus in requestStatusValues" :key="requestStatus" :value="requestStatus">{{ requestStatus }}</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Instructor Availability</label>
            <select
              class="form-control"
              id="session-instructorAvailability"
              data-cy="instructorAvailability"
              name="instructorAvailability"
              v-model="session.instructorAvailability"
            >
              <option :value="null"></option>
              <option
                :value="
                  session.instructorAvailability && instructorAvailabilityOption.id === session.instructorAvailability.id
                    ? session.instructorAvailability
                    : instructorAvailabilityOption
                "
                v-for="instructorAvailabilityOption in instructorAvailabilities"
                :key="instructorAvailabilityOption.id"
              >
                {{ instructorAvailabilityOption.title }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Location Availability</label>
            <select
              class="form-control"
              id="session-locationAvailability"
              data-cy="locationAvailability"
              name="locationAvailability"
              v-model="session.locationAvailability"
            >
              <option :value="null"></option>
              <option
                :value="
                  session.locationAvailability && locationAvailabilityOption.id === session.locationAvailability.id
                    ? session.locationAvailability
                    : locationAvailabilityOption
                "
                v-for="locationAvailabilityOption in locationAvailabilities"
                :key="locationAvailabilityOption.id"
              >
                {{ locationAvailabilityOption.title }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Requested Course</label>
            <select
              class="form-control"
              id="session-requestedCourse"
              data-cy="requestedCourse"
              name="requestedCourse"
              v-model="session.requestedCourse"
            >
              <option :value="null"></option>
              <option
                :value="
                  session.requestedCourse && requestedCourseOption.id === session.requestedCourse.id
                    ? session.requestedCourse
                    : requestedCourseOption
                "
                v-for="requestedCourseOption in requestedCourses"
                :key="requestedCourseOption.id"
              >
                {{ requestedCourseOption.id }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="session">Freelancer</label>
            <select class="form-control" id="session-freelancer" data-cy="freelancer" name="freelancer" v-model="session.freelancer">
              <option :value="null"></option>
              <option
                :value="session.freelancer && freelancerOption.id === session.freelancer.id ? session.freelancer : freelancerOption"
                v-for="freelancerOption in freelancers"
                :key="freelancerOption.id"
              >
                {{ freelancerOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./session-update.component.ts"></script>
