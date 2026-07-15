import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IFreelancer } from '@/shared/model/freelancer.model';

import FreelancerService from './freelancer.service';

export default defineComponent({
  name: 'Freelancer',
  setup() {
    const freelancerService = inject('freelancerService', () => new FreelancerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const freelancers: Ref<IFreelancer[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveFreelancers = async () => {
      isFetching.value = true;
      try {
        const res = await freelancerService().retrieve();
        freelancers.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveFreelancers();
    };

    onMounted(async () => {
      await retrieveFreelancers();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFreelancer) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeFreelancer = async () => {
      try {
        await freelancerService().delete(removeId.value);
        const message = `A Freelancer is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveFreelancers();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      freelancers,
      handleSyncList,
      isFetching,
      retrieveFreelancers,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeFreelancer,
    };
  },
});
