import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ILocationAvailability } from '@/shared/model/location-availability.model';

import LocationAvailabilityService from './location-availability.service';

export default defineComponent({
  name: 'LocationAvailabilityDetails',
  setup() {
    const dateFormat = useDateFormat();
    const locationAvailabilityService = inject('locationAvailabilityService', () => new LocationAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const locationAvailability: Ref<ILocationAvailability> = ref({});

    const retrieveLocationAvailability = async locationAvailabilityId => {
      try {
        const res = await locationAvailabilityService().find(locationAvailabilityId);
        locationAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.locationAvailabilityId) {
      retrieveLocationAvailability(route.params.locationAvailabilityId);
    }

    return {
      ...dateFormat,
      alertService,
      locationAvailability,

      previousState,
    };
  },
});
