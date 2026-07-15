<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.trainingRequest.home.createOrEditLabel" data-cy="TrainingRequestCreateUpdateHeading">
          Create or edit a Training Request
        </h2>
        <div>
          <div class="mb-3" v-if="trainingRequest.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="trainingRequest.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="training-request">Request Date</label>
            <div class="d-flex">
              <input
                id="training-request-requestDate"
                data-cy="requestDate"
                type="datetime-local"
                class="form-control"
                name="requestDate"
                :class="{ valid: !v$.requestDate.$invalid, invalid: v$.requestDate.$invalid }"
                :value="convertDateTimeFromServer(v$.requestDate.$model)"
                @change="updateInstantField('requestDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="training-request">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="training-request-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
              required
            />
            <div v-if="v$.description.$anyDirty && v$.description.$invalid">
              <small class="form-text text-danger" v-for="error of v$.description.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="training-request">Customer</label>
            <select
              class="form-control"
              id="training-request-customer"
              data-cy="customer"
              name="customer"
              v-model="trainingRequest.customer"
            >
              <option :value="null"></option>
              <option
                :value="
                  trainingRequest.customer && customerOption.id === trainingRequest.customer.id ? trainingRequest.customer : customerOption
                "
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.name }}
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
<script lang="ts" src="./training-request-update.component.ts"></script>
