import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IRequestedCourse } from '@/shared/model/requested-course.model';

import RequestedCourseService from './requested-course.service';

export default defineComponent({
  name: 'RequestedCourse',
  setup() {
    const dateFormat = useDateFormat();
    const requestedCourseService = inject('requestedCourseService', () => new RequestedCourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const requestedCourses: Ref<IRequestedCourse[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveRequestedCourses = async () => {
      isFetching.value = true;
      try {
        const res = await requestedCourseService().retrieve();
        requestedCourses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveRequestedCourses();
    };

    onMounted(async () => {
      await retrieveRequestedCourses();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IRequestedCourse) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeRequestedCourse = async () => {
      try {
        await requestedCourseService().delete(removeId.value);
        const message = `A RequestedCourse is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveRequestedCourses();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      requestedCourses,
      handleSyncList,
      isFetching,
      retrieveRequestedCourses,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeRequestedCourse,
    };
  },
});
