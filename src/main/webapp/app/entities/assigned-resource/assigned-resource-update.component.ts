import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import ResourceAvailabilityService from '@/entities/resource-availability/resource-availability.service';
import SessionService from '@/entities/session/session.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { AssignedResource, type IAssignedResource } from '@/shared/model/assigned-resource.model';
import { type IResourceAvailability } from '@/shared/model/resource-availability.model';
import { type ISession } from '@/shared/model/session.model';

import AssignedResourceService from './assigned-resource.service';

export default defineComponent({
  name: 'AssignedResourceUpdate',
  setup() {
    const assignedResourceService = inject('assignedResourceService', () => new AssignedResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const assignedResource: Ref<IAssignedResource> = ref(new AssignedResource());

    const resourceAvailabilityService = inject('resourceAvailabilityService', () => new ResourceAvailabilityService());

    const resourceAvailabilities: Ref<IResourceAvailability[]> = ref([]);

    const sessionService = inject('sessionService', () => new SessionService());

    const sessions: Ref<ISession[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAssignedResource = async assignedResourceId => {
      try {
        const res = await assignedResourceService().find(assignedResourceId);
        assignedResource.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.assignedResourceId) {
      retrieveAssignedResource(route.params.assignedResourceId);
    }

    const initRelationships = () => {
      resourceAvailabilityService()
        .retrieve()
        .then(res => {
          resourceAvailabilities.value = res.data;
        });
      sessionService()
        .retrieve()
        .then(res => {
          sessions.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      resourceAvailability: {},
      session: {},
    };
    const v$ = useVuelidate(validationRules, assignedResource as any);
    v$.value.$validate();

    return {
      assignedResourceService,
      alertService,
      assignedResource,
      previousState,
      isSaving,
      currentLanguage,
      resourceAvailabilities,
      sessions,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.assignedResource.id) {
        this.assignedResourceService()
          .update(this.assignedResource)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A AssignedResource is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.assignedResourceService()
          .create(this.assignedResource)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A AssignedResource is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
