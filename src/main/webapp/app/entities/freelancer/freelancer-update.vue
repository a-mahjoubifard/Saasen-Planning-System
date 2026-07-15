<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.freelancer.home.createOrEditLabel" data-cy="FreelancerCreateUpdateHeading">Create or edit a Freelancer</h2>
        <div>
          <div class="mb-3" v-if="freelancer.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="freelancer.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="freelancer-name"
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
            <label class="form-control-label" for="freelancer">Contact</label>
            <input
              type="text"
              class="form-control"
              name="contact"
              id="freelancer-contact"
              data-cy="contact"
              :class="{ valid: !v$.contact.$invalid, invalid: v$.contact.$invalid }"
              v-model="v$.contact.$model"
            />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="freelancer">Qualification</label>
            <input
              type="text"
              class="form-control"
              name="qualification"
              id="freelancer-qualification"
              data-cy="qualification"
              :class="{ valid: !v$.qualification.$invalid, invalid: v$.qualification.$invalid }"
              v-model="v$.qualification.$model"
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
<script lang="ts" src="./freelancer-update.component.ts"></script>
