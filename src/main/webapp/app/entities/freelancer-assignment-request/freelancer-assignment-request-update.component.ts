import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import FreelancerService from '@/entities/freelancer/freelancer.service';
import SessionService from '@/entities/session/session.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { FreelancerRequestStatus } from '@/shared/model/enumerations/freelancer-request-status.model';
import { FreelancerAssignmentRequest, type IFreelancerAssignmentRequest } from '@/shared/model/freelancer-assignment-request.model';
import { type IFreelancer } from '@/shared/model/freelancer.model';
import { type ISession } from '@/shared/model/session.model';

import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';

export default defineComponent({
  name: 'FreelancerAssignmentRequestUpdate',
  setup() {
    const freelancerAssignmentRequestService = inject('freelancerAssignmentRequestService', () => new FreelancerAssignmentRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const freelancerAssignmentRequest: Ref<IFreelancerAssignmentRequest> = ref(new FreelancerAssignmentRequest());

    const freelancerService = inject('freelancerService', () => new FreelancerService());

    const freelancers: Ref<IFreelancer[]> = ref([]);

    const sessionService = inject('sessionService', () => new SessionService());

    const sessions: Ref<ISession[]> = ref([]);
    const freelancerRequestStatusValues: Ref<string[]> = ref(Object.keys(FreelancerRequestStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveFreelancerAssignmentRequest = async freelancerAssignmentRequestId => {
      try {
        const res = await freelancerAssignmentRequestService().find(freelancerAssignmentRequestId);
        res.requestedAt = new Date(res.requestedAt);
        freelancerAssignmentRequest.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.freelancerAssignmentRequestId) {
      retrieveFreelancerAssignmentRequest(route.params.freelancerAssignmentRequestId);
    }

    const initRelationships = () => {
      freelancerService()
        .retrieve()
        .then(res => {
          freelancers.value = res.data;
        });
      sessionService()
        .retrieve()
        .then(res => {
          sessions.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      requestedAt: {},
      status: {
        required: validations.required('This field is required.'),
      },
      freelancer: {},
      session: {},
    };
    const v$ = useVuelidate(validationRules, freelancerAssignmentRequest as any);
    v$.value.$validate();

    return {
      freelancerAssignmentRequestService,
      alertService,
      freelancerAssignmentRequest,
      previousState,
      freelancerRequestStatusValues,
      isSaving,
      currentLanguage,
      freelancers,
      sessions,
      v$,
      ...useDateFormat({ entityRef: freelancerAssignmentRequest }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.freelancerAssignmentRequest.id) {
        this.freelancerAssignmentRequestService()
          .update(this.freelancerAssignmentRequest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A FreelancerAssignmentRequest is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.freelancerAssignmentRequestService()
          .create(this.freelancerAssignmentRequest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A FreelancerAssignmentRequest is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
