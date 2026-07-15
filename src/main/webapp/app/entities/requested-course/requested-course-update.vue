<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.requestedCourse.home.createOrEditLabel" data-cy="RequestedCourseCreateUpdateHeading">
          Create or edit a Requested Course
        </h2>
        <div>
          <div class="mb-3" v-if="requestedCourse.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="requestedCourse.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Number Of Participants</label>
            <input
              type="number"
              class="form-control"
              name="numberOfParticipants"
              id="requested-course-numberOfParticipants"
              data-cy="numberOfParticipants"
              :class="{ valid: !v$.numberOfParticipants.$invalid, invalid: v$.numberOfParticipants.$invalid }"
              v-model.number="v$.numberOfParticipants.$model"
              required
            />
            <div v-if="v$.numberOfParticipants.$anyDirty && v$.numberOfParticipants.$invalid">
              <small class="form-text text-danger" v-for="error of v$.numberOfParticipants.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Preferred Start Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="requested-course-preferredStartDate"
                  v-model="v$.preferredStartDate.$model"
                  name="preferredStartDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="requested-course-preferredStartDate"
                data-cy="preferredStartDate"
                type="text"
                class="form-control"
                name="preferredStartDate"
                :class="{ valid: !v$.preferredStartDate.$invalid, invalid: v$.preferredStartDate.$invalid }"
                v-model="v$.preferredStartDate.$model"
              />
            </b-input-group>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Preferred Location</label>
            <input
              type="text"
              class="form-control"
              name="preferredLocation"
              id="requested-course-preferredLocation"
              data-cy="preferredLocation"
              :class="{ valid: !v$.preferredLocation.$invalid, invalid: v$.preferredLocation.$invalid }"
              v-model="v$.preferredLocation.$model"
              required
            />
            <div v-if="v$.preferredLocation.$anyDirty && v$.preferredLocation.$invalid">
              <small class="form-text text-danger" v-for="error of v$.preferredLocation.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="requested-course-status"
              data-cy="status"
            >
              <option v-for="requestStatus in requestStatusValues" :key="requestStatus" :value="requestStatus">{{ requestStatus }}</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Actual Start Date</label>
            <div class="d-flex">
              <input
                id="requested-course-actualStartDate"
                data-cy="actualStartDate"
                type="datetime-local"
                class="form-control"
                name="actualStartDate"
                :class="{ valid: !v$.actualStartDate.$invalid, invalid: v$.actualStartDate.$invalid }"
                :value="convertDateTimeFromServer(v$.actualStartDate.$model)"
                @change="updateInstantField('actualStartDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Actual End Date</label>
            <div class="d-flex">
              <input
                id="requested-course-actualEndDate"
                data-cy="actualEndDate"
                type="datetime-local"
                class="form-control"
                name="actualEndDate"
                :class="{ valid: !v$.actualEndDate.$invalid, invalid: v$.actualEndDate.$invalid }"
                :value="convertDateTimeFromServer(v$.actualEndDate.$model)"
                @change="updateInstantField('actualEndDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Training Request</label>
            <select
              class="form-control"
              id="requested-course-trainingRequest"
              data-cy="trainingRequest"
              name="trainingRequest"
              v-model="requestedCourse.trainingRequest"
            >
              <option :value="null"></option>
              <option
                :value="
                  requestedCourse.trainingRequest && trainingRequestOption.id === requestedCourse.trainingRequest.id
                    ? requestedCourse.trainingRequest
                    : trainingRequestOption
                "
                v-for="trainingRequestOption in trainingRequests"
                :key="trainingRequestOption.id"
              >
                {{ trainingRequestOption.id }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="requested-course">Course</label>
            <select class="form-control" id="requested-course-course" data-cy="course" name="course" v-model="requestedCourse.course">
              <option :value="null"></option>
              <option
                :value="requestedCourse.course && courseOption.id === requestedCourse.course.id ? requestedCourse.course : courseOption"
                v-for="courseOption in courses"
                :key="courseOption.id"
              >
                {{ courseOption.title }}
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
<script lang="ts" src="./requested-course-update.component.ts"></script>
