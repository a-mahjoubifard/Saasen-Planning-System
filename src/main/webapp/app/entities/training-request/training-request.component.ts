import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrainingRequest } from '@/shared/model/training-request.model';

import TrainingRequestService from './training-request.service';

export default defineComponent({
  name: 'TrainingRequest',
  setup() {
    const dateFormat = useDateFormat();
    const trainingRequestService = inject('trainingRequestService', () => new TrainingRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trainingRequests: Ref<ITrainingRequest[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveTrainingRequests = async () => {
      isFetching.value = true;
      try {
        const res = await trainingRequestService().retrieve();
        trainingRequests.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTrainingRequests();
    };

    onMounted(async () => {
      await retrieveTrainingRequests();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITrainingRequest) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTrainingRequest = async () => {
      try {
        await trainingRequestService().delete(removeId.value);
        const message = `A TrainingRequest is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTrainingRequests();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      trainingRequests,
      handleSyncList,
      isFetching,
      retrieveTrainingRequests,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrainingRequest,
    };
  },
});
