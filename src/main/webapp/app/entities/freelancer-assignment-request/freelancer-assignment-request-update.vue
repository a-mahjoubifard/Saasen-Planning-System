<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.freelancerAssignmentRequest.home.createOrEditLabel" data-cy="FreelancerAssignmentRequestCreateUpdateHeading">
          Create or edit a Freelancer Assignment Request
        </h2>
        <div>
          <div class="mb-3" v-if="freelancerAssignmentRequest.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="freelancerAssignmentRequest.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer-assignment-request">Requested At</label>
            <div class="d-flex">
              <input
                id="freelancer-assignment-request-requestedAt"
                data-cy="requestedAt"
                type="datetime-local"
                class="form-control"
                name="requestedAt"
                :class="{ valid: !v$.requestedAt.$invalid, invalid: v$.requestedAt.$invalid }"
                :value="convertDateTimeFromServer(v$.requestedAt.$model)"
                @change="updateInstantField('requestedAt', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer-assignment-request">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="freelancer-assignment-request-status"
              data-cy="status"
              required
            >
              <option
                v-for="freelancerRequestStatus in freelancerRequestStatusValues"
                :key="freelancerRequestStatus"
                :value="freelancerRequestStatus"
              >
                {{ freelancerRequestStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer-assignment-request">Freelancer</label>
            <select
              class="form-control"
              id="freelancer-assignment-request-freelancer"
              data-cy="freelancer"
              name="freelancer"
              v-model="freelancerAssignmentRequest.freelancer"
            >
              <option :value="null"></option>
              <option
                :value="
                  freelancerAssignmentRequest.freelancer && freelancerOption.id === freelancerAssignmentRequest.freelancer.id
                    ? freelancerAssignmentRequest.freelancer
                    : freelancerOption
                "
                v-for="freelancerOption in freelancers"
                :key="freelancerOption.id"
              >
                {{ freelancerOption.name }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer-assignment-request">Session</label>
            <select
              class="form-control"
              id="freelancer-assignment-request-session"
              data-cy="session"
              name="session"
              v-model="freelancerAssignmentRequest.session"
            >
              <option :value="null"></option>
              <option
                :value="
                  freelancerAssignmentRequest.session && sessionOption.id === freelancerAssignmentRequest.session.id
                    ? freelancerAssignmentRequest.session
                    : sessionOption
                "
                v-for="sessionOption in sessions"
                :key="sessionOption.id"
              >
                {{ sessionOption.id }}
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
<script lang="ts" src="./freelancer-assignment-request-update.component.ts"></script>
