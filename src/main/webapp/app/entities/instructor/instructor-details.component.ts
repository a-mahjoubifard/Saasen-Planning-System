import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IInstructor } from '@/shared/model/instructor.model';

import InstructorService from './instructor.service';

export default defineComponent({
  name: 'InstructorDetails',
  setup() {
    const instructorService = inject('instructorService', () => new InstructorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const instructor: Ref<IInstructor> = ref({});

    const retrieveInstructor = async instructorId => {
      try {
        const res = await instructorService().find(instructorId);
        instructor.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instructorId) {
      retrieveInstructor(route.params.instructorId);
    }

    return {
      alertService,
      instructor,

      previousState,
    };
  },
});
