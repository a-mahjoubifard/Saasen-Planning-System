import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IFreelancerAssignmentRequest } from '@/shared/model/freelancer-assignment-request.model';

import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';

export default defineComponent({
  name: 'FreelancerAssignmentRequest',
  setup() {
    const dateFormat = useDateFormat();
    const freelancerAssignmentRequestService = inject('freelancerAssignmentRequestService', () => new FreelancerAssignmentRequestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const freelancerAssignmentRequests: Ref<IFreelancerAssignmentRequest[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveFreelancerAssignmentRequests = async () => {
      isFetching.value = true;
      try {
        const res = await freelancerAssignmentRequestService().retrieve();
        freelancerAssignmentRequests.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveFreelancerAssignmentRequests();
    };

    onMounted(async () => {
      await retrieveFreelancerAssignmentRequests();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFreelancerAssignmentRequest) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeFreelancerAssignmentRequest = async () => {
      try {
        await freelancerAssignmentRequestService().delete(removeId.value);
        const message = `A FreelancerAssignmentRequest is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveFreelancerAssignmentRequests();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      freelancerAssignmentRequests,
      handleSyncList,
      isFetching,
      retrieveFreelancerAssignmentRequests,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeFreelancerAssignmentRequest,
    };
  },
});
