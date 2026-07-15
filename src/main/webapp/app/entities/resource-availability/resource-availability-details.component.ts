import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IResourceAvailability } from '@/shared/model/resource-availability.model';

import ResourceAvailabilityService from './resource-availability.service';

export default defineComponent({
  name: 'ResourceAvailabilityDetails',
  setup() {
    const dateFormat = useDateFormat();
    const resourceAvailabilityService = inject('resourceAvailabilityService', () => new ResourceAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const resourceAvailability: Ref<IResourceAvailability> = ref({});

    const retrieveResourceAvailability = async resourceAvailabilityId => {
      try {
        const res = await resourceAvailabilityService().find(resourceAvailabilityId);
        resourceAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.resourceAvailabilityId) {
      retrieveResourceAvailability(route.params.resourceAvailabilityId);
    }

    return {
      ...dateFormat,
      alertService,
      resourceAvailability,

      previousState,
    };
  },
});
