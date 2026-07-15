import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IFreelancerAssignmentRequest } from '@/shared/model/freelancer-assignment-request.model';

import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';

export default defineComponent({
  name: 'FreelancerAssignmentRequestDetails',
  setup() {
    const dateFormat = useDateFormat();
    const freelancerAssignmentRequestService = inject('freelancerAssignmentRequestService', () => new FreelancerAssignmentRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const freelancerAssignmentRequest: Ref<IFreelancerAssignmentRequest> = ref({});

    const retrieveFreelancerAssignmentRequest = async freelancerAssignmentRequestId => {
      try {
        const res = await freelancerAssignmentRequestService().find(freelancerAssignmentRequestId);
        freelancerAssignmentRequest.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.freelancerAssignmentRequestId) {
      retrieveFreelancerAssignmentRequest(route.params.freelancerAssignmentRequestId);
    }

    return {
      ...dateFormat,
      alertService,
      freelancerAssignmentRequest,

      previousState,
    };
  },
});
