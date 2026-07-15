import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import LocationService from '@/entities/location/location.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ILocationAvailability, LocationAvailability } from '@/shared/model/location-availability.model';
import { type ILocation } from '@/shared/model/location.model';

import LocationAvailabilityService from './location-availability.service';

export default defineComponent({
  name: 'LocationAvailabilityUpdate',
  setup() {
    const locationAvailabilityService = inject('locationAvailabilityService', () => new LocationAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const locationAvailability: Ref<ILocationAvailability> = ref(new LocationAvailability());

    const locationService = inject('locationService', () => new LocationService());

    const locations: Ref<ILocation[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveLocationAvailability = async locationAvailabilityId => {
      try {
        const res = await locationAvailabilityService().find(locationAvailabilityId);
        res.availableFrom = new Date(res.availableFrom);
        res.availableTo = new Date(res.availableTo);
        locationAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.locationAvailabilityId) {
      retrieveLocationAvailability(route.params.locationAvailabilityId);
    }

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then(res => {
          locations.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('This field is required.'),
      },
      availableFrom: {},
      availableTo: {},
      location: {},
      session: {},
    };
    const v$ = useVuelidate(validationRules, locationAvailability as any);
    v$.value.$validate();

    return {
      locationAvailabilityService,
      alertService,
      locationAvailability,
      previousState,
      isSaving,
      currentLanguage,
      locations,
      v$,
      ...useDateFormat({ entityRef: locationAvailability }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.locationAvailability.id) {
        this.locationAvailabilityService()
          .update(this.locationAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A LocationAvailability is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.locationAvailabilityService()
          .create(this.locationAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A LocationAvailability is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
