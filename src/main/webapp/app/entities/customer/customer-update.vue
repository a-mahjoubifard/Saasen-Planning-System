<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.customer.home.createOrEditLabel" data-cy="CustomerCreateUpdateHeading">Create or edit a Customer</h2>
        <div>
          <div class="mb-3" v-if="customer.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customer.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="customer-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">Contact Info</label>
            <input
              type="text"
              class="form-control"
              name="contactInfo"
              id="customer-contactInfo"
              data-cy="contactInfo"
              :class="{ valid: !v$.contactInfo.$invalid, invalid: v$.contactInfo.$invalid }"
              v-model="v$.contactInfo.$model"
            />
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
<script lang="ts" src="./customer-update.component.ts"></script>
