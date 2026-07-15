<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.resource.home.createOrEditLabel" data-cy="ResourceCreateUpdateHeading">Create or edit a Resource</h2>
        <div>
          <div class="mb-3" v-if="resource.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="resource.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="resource">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="resource-name"
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
            <label class="form-control-label" for="resource">Resource Type</label>
            <input
              type="text"
              class="form-control"
              name="resourceType"
              id="resource-resourceType"
              data-cy="resourceType"
              :class="{ valid: !v$.resourceType.$invalid, invalid: v$.resourceType.$invalid }"
              v-model="v$.resourceType.$model"
            />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="resource">Status</label>
            <input
              type="text"
              class="form-control"
              name="status"
              id="resource-status"
              data-cy="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
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
<script lang="ts" src="./resource-update.component.ts"></script>
