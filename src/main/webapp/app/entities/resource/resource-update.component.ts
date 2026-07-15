import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { type IResource, Resource } from '@/shared/model/resource.model';

import ResourceService from './resource.service';

export default defineComponent({
  name: 'ResourceUpdate',
  setup() {
    const resourceService = inject('resourceService', () => new ResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const resource: Ref<IResource> = ref(new Resource());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveResource = async resourceId => {
      try {
        const res = await resourceService().find(resourceId);
        resource.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.resourceId) {
      retrieveResource(route.params.resourceId);
    }

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      resourceType: {},
      status: {},
    };
    const v$ = useVuelidate(validationRules, resource as any);
    v$.value.$validate();

    return {
      resourceService,
      alertService,
      resource,
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
      if (this.resource.id) {
        this.resourceService()
          .update(this.resource)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Resource is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.resourceService()
          .create(this.resource)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Resource is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
