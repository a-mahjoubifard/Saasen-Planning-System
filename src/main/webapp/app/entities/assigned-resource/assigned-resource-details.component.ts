import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IAssignedResource } from '@/shared/model/assigned-resource.model';

import AssignedResourceService from './assigned-resource.service';

export default defineComponent({
  name: 'AssignedResourceDetails',
  setup() {
    const assignedResourceService = inject('assignedResourceService', () => new AssignedResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const assignedResource: Ref<IAssignedResource> = ref({});

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

    return {
      alertService,
      assignedResource,

      previousState,
    };
  },
});
