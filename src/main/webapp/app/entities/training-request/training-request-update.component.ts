import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerService from '@/entities/customer/customer.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ICustomer } from '@/shared/model/customer.model';
import { type ITrainingRequest, TrainingRequest } from '@/shared/model/training-request.model';

import TrainingRequestService from './training-request.service';

export default defineComponent({
  name: 'TrainingRequestUpdate',
  setup() {
    const trainingRequestService = inject('trainingRequestService', () => new TrainingRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trainingRequest: Ref<ITrainingRequest> = ref(new TrainingRequest());

    const customerService = inject('customerService', () => new CustomerService());

    const customers: Ref<ICustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrainingRequest = async trainingRequestId => {
      try {
        const res = await trainingRequestService().find(trainingRequestId);
        res.requestDate = new Date(res.requestDate);
        trainingRequest.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trainingRequestId) {
      retrieveTrainingRequest(route.params.trainingRequestId);
    }

    const initRelationships = () => {
      customerService()
        .retrieve()
        .then(res => {
          customers.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      requestDate: {},
      description: {
        required: validations.required('This field is required.'),
      },
      customer: {},
    };
    const v$ = useVuelidate(validationRules, trainingRequest as any);
    v$.value.$validate();

    return {
      trainingRequestService,
      alertService,
      trainingRequest,
      previousState,
      isSaving,
      currentLanguage,
      customers,
      v$,
      ...useDateFormat({ entityRef: trainingRequest }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trainingRequest.id) {
        this.trainingRequestService()
          .update(this.trainingRequest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A TrainingRequest is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trainingRequestService()
          .create(this.trainingRequest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A TrainingRequest is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
