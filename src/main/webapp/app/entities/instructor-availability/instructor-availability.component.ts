import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IInstructorAvailability } from '@/shared/model/instructor-availability.model';

import InstructorAvailabilityService from './instructor-availability.service';

export default defineComponent({
  name: 'InstructorAvailability',
  setup() {
    const dateFormat = useDateFormat();
    const instructorAvailabilityService = inject('instructorAvailabilityService', () => new InstructorAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instructorAvailabilities: Ref<IInstructorAvailability[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInstructorAvailabilitys = async () => {
      isFetching.value = true;
      try {
        const res = await instructorAvailabilityService().retrieve();
        instructorAvailabilities.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInstructorAvailabilitys();
    };

    onMounted(async () => {
      await retrieveInstructorAvailabilitys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInstructorAvailability) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInstructorAvailability = async () => {
      try {
        await instructorAvailabilityService().delete(removeId.value);
        const message = `A InstructorAvailability is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInstructorAvailabilitys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      instructorAvailabilities,
      handleSyncList,
      isFetching,
      retrieveInstructorAvailabilitys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInstructorAvailability,
    };
  },
});
