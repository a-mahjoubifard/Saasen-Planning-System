import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { type ICourse } from '@/shared/model/course.model';

import CourseService from './course.service';

export default defineComponent({
  name: 'Course',
  setup() {
    const courseService = inject('courseService', () => new CourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const courses: Ref<ICourse[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCourses = async () => {
      isFetching.value = true;
      try {
        const res = await courseService().retrieve();
        courses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCourses();
    };

    onMounted(async () => {
      await retrieveCourses();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICourse) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCourse = async () => {
      try {
        await courseService().delete(removeId.value);
        const message = `A Course is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCourses();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      courses,
      handleSyncList,
      isFetching,
      retrieveCourses,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCourse,
    };
  },
});
