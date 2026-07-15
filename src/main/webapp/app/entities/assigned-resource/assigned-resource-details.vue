<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="assignedResource">
        <h2 class="jh-entity-heading" data-cy="assignedResourceDetailsHeading">
          <span>Assigned ResourceAssignedResource</span> {{ assignedResource.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Resource Availability</span>
          </dt>
          <dd>
            <div v-if="assignedResource.resourceAvailability">
              <router-link
                :to="{ name: 'ResourceAvailabilityView', params: { resourceAvailabilityId: assignedResource.resourceAvailability.id } }"
                >{{ assignedResource.resourceAvailability.id }}</router-link
              >
            </div>
          </dd>
          <dt>
            <span>Session</span>
          </dt>
          <dd>
            <div v-if="assignedResource.session">
              <router-link :to="{ name: 'SessionView', params: { sessionId: assignedResource.session.id } }">{{
                assignedResource.session.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="assignedResource.id"
          :to="{ name: 'AssignedResourceEdit', params: { assignedResourceId: assignedResource.id } }"
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

<script lang="ts" src="./assigned-resource-details.component.ts"></script>
