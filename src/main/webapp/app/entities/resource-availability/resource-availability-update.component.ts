import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import ResourceService from '@/entities/resource/resource.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type IResourceAvailability, ResourceAvailability } from '@/shared/model/resource-availability.model';
import { type IResource } from '@/shared/model/resource.model';

import ResourceAvailabilityService from './resource-availability.service';

export default defineComponent({
  name: 'ResourceAvailabilityUpdate',
  setup() {
    const resourceAvailabilityService = inject('resourceAvailabilityService', () => new ResourceAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const resourceAvailability: Ref<IResourceAvailability> = ref(new ResourceAvailability());

    const resourceService = inject('resourceService', () => new ResourceService());

    const resources: Ref<IResource[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveResourceAvailability = async resourceAvailabilityId => {
      try {
        const res = await resourceAvailabilityService().find(resourceAvailabilityId);
        res.availableFrom = new Date(res.availableFrom);
        res.availableTo = new Date(res.availableTo);
        resourceAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.resourceAvailabilityId) {
      retrieveResourceAvailability(route.params.resourceAvailabilityId);
    }

    const initRelationships = () => {
      resourceService()
        .retrieve()
        .then(res => {
          resources.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      availableFrom: {},
      availableTo: {},
      resource: {},
      assignedResource: {},
    };
    const v$ = useVuelidate(validationRules, resourceAvailability as any);
    v$.value.$validate();

    return {
      resourceAvailabilityService,
      alertService,
      resourceAvailability,
      previousState,
      isSaving,
      currentLanguage,
      resources,
      v$,
      ...useDateFormat({ entityRef: resourceAvailability }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.resourceAvailability.id) {
        this.resourceAvailabilityService()
          .update(this.resourceAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A ResourceAvailability is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.resourceAvailabilityService()
          .create(this.resourceAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A ResourceAvailability is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
