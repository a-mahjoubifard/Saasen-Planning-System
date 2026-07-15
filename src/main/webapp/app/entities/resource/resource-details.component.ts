import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IResource } from '@/shared/model/resource.model';

import ResourceService from './resource.service';

export default defineComponent({
  name: 'ResourceDetails',
  setup() {
    const resourceService = inject('resourceService', () => new ResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const resource: Ref<IResource> = ref({});

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

    return {
      alertService,
      resource,

      previousState,
    };
  },
});
