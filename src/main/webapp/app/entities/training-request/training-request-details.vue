<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="trainingRequest">
        <h2 class="jh-entity-heading" data-cy="trainingRequestDetailsHeading">
          <span>Training RequestTrainingRequest</span> {{ trainingRequest.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Request Date</span>
          </dt>
          <dd>
            <span v-if="trainingRequest.requestDate">{{ formatDateLong(trainingRequest.requestDate) }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ trainingRequest.description }}</span>
          </dd>
          <dt>
            <span>Customer</span>
          </dt>
          <dd>
            <div v-if="trainingRequest.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: trainingRequest.customer.id } }">{{
                trainingRequest.customer.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="trainingRequest.id"
          :to="{ name: 'TrainingRequestEdit', params: { trainingRequestId: trainingRequest.id } }"
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

<script lang="ts" src="./training-request-details.component.ts"></script>
