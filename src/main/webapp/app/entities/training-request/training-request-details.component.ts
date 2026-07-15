import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrainingRequest } from '@/shared/model/training-request.model';

import TrainingRequestService from './training-request.service';

export default defineComponent({
  name: 'TrainingRequestDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trainingRequestService = inject('trainingRequestService', () => new TrainingRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trainingRequest: Ref<ITrainingRequest> = ref({});

    const retrieveTrainingRequest = async trainingRequestId => {
      try {
        const res = await trainingRequestService().find(trainingRequestId);
        trainingRequest.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trainingRequestId) {
      retrieveTrainingRequest(route.params.trainingRequestId);
    }

    return {
      ...dateFormat,
      alertService,
      trainingRequest,

      previousState,
    };
  },
});
