<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="resource">
        <h2 class="jh-entity-heading" data-cy="resourceDetailsHeading"><span>ResourceResource</span> {{ resource.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ resource.name }}</span>
          </dd>
          <dt>
            <span>Resource Type</span>
          </dt>
          <dd>
            <span>{{ resource.resourceType }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ resource.status }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="resource.id" :to="{ name: 'ResourceEdit', params: { resourceId: resource.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./resource-details.component.ts"></script>
