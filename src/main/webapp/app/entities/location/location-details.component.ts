import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type ILocation } from '@/shared/model/location.model';

import LocationService from './location.service';

export default defineComponent({
  name: 'LocationDetails',
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const location: Ref<ILocation> = ref({});

    const retrieveLocation = async locationId => {
      try {
        const res = await locationService().find(locationId);
        location.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.locationId) {
      retrieveLocation(route.params.locationId);
    }

    return {
      alertService,
      location,

      previousState,
    };
  },
});
