import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IInstructor } from '@/shared/model/instructor.model';

import InstructorService from './instructor.service';

export default defineComponent({
  name: 'Instructor',
  setup() {
    const instructorService = inject('instructorService', () => new InstructorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instructors: Ref<IInstructor[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInstructors = async () => {
      isFetching.value = true;
      try {
        const res = await instructorService().retrieve();
        instructors.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInstructors();
    };

    onMounted(async () => {
      await retrieveInstructors();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInstructor) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInstructor = async () => {
      try {
        await instructorService().delete(removeId.value);
        const message = `A Instructor is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInstructors();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      instructors,
      handleSyncList,
      isFetching,
      retrieveInstructors,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInstructor,
    };
  },
});
