import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IRequestedCourse } from '@/shared/model/requested-course.model';

import RequestedCourseService from './requested-course.service';

export default defineComponent({
  name: 'RequestedCourseDetails',
  setup() {
    const dateFormat = useDateFormat();
    const requestedCourseService = inject('requestedCourseService', () => new RequestedCourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const requestedCourse: Ref<IRequestedCourse> = ref({});

    const retrieveRequestedCourse = async requestedCourseId => {
      try {
        const res = await requestedCourseService().find(requestedCourseId);
        requestedCourse.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.requestedCourseId) {
      retrieveRequestedCourse(route.params.requestedCourseId);
    }

    return {
      ...dateFormat,
      alertService,
      requestedCourse,

      previousState,
    };
  },
});
