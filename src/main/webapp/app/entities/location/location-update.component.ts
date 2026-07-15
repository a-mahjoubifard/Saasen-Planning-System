import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { type ILocation, Location } from '@/shared/model/location.model';

import LocationService from './location.service';

export default defineComponent({
  name: 'LocationUpdate',
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const location: Ref<ILocation> = ref(new Location());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      address: {},
      capacity: {},
      equipmentType: {},
    };
    const v$ = useVuelidate(validationRules, location as any);
    v$.value.$validate();

    return {
      locationService,
      alertService,
      location,
      previousState,
      isSaving,
      currentLanguage,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.location.id) {
        this.locationService()
          .update(this.location)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Location is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.locationService()
          .create(this.location)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Location is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
