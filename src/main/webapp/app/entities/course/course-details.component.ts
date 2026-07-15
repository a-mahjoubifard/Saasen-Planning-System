import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type ICourse } from '@/shared/model/course.model';

import CourseService from './course.service';

export default defineComponent({
  name: 'CourseDetails',
  setup() {
    const courseService = inject('courseService', () => new CourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const course: Ref<ICourse> = ref({});

    const retrieveCourse = async courseId => {
      try {
        const res = await courseService().find(courseId);
        course.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseId) {
      retrieveCourse(route.params.courseId);
    }

    return {
      alertService,
      course,

      previousState,
    };
  },
});
