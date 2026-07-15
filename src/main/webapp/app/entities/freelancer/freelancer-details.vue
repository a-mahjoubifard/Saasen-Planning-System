<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="freelancer">
        <h2 class="jh-entity-heading" data-cy="freelancerDetailsHeading"><span>FreelancerFreelancer</span> {{ freelancer.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ freelancer.name }}</span>
          </dd>
          <dt>
            <span>Contact</span>
          </dt>
          <dd>
            <span>{{ freelancer.contact }}</span>
          </dd>
          <dt>
            <span>Qualification</span>
          </dt>
          <dd>
            <span>{{ freelancer.qualification }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="freelancer.id"
          :to="{ name: 'FreelancerEdit', params: { freelancerId: freelancer.id } }"
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

<script lang="ts" src="./freelancer-details.component.ts"></script>
