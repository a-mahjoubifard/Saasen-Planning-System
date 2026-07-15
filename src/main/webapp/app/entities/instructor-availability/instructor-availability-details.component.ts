import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IInstructorAvailability } from '@/shared/model/instructor-availability.model';

import InstructorAvailabilityService from './instructor-availability.service';

export default defineComponent({
  name: 'InstructorAvailabilityDetails',
  setup() {
    const dateFormat = useDateFormat();
    const instructorAvailabilityService = inject('instructorAvailabilityService', () => new InstructorAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const instructorAvailability: Ref<IInstructorAvailability> = ref({});

    const retrieveInstructorAvailability = async instructorAvailabilityId => {
      try {
        const res = await instructorAvailabilityService().find(instructorAvailabilityId);
        instructorAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instructorAvailabilityId) {
      retrieveInstructorAvailability(route.params.instructorAvailabilityId);
    }

    return {
      ...dateFormat,
      alertService,
      instructorAvailability,

      previousState,
    };
  },
});
