import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IResource } from '@/shared/model/resource.model';

import ResourceService from './resource.service';

export default defineComponent({
  name: 'Resource',
  setup() {
    const resourceService = inject('resourceService', () => new ResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const resources: Ref<IResource[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveResources = async () => {
      isFetching.value = true;
      try {
        const res = await resourceService().retrieve();
        resources.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveResources();
    };

    onMounted(async () => {
      await retrieveResources();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IResource) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeResource = async () => {
      try {
        await resourceService().delete(removeId.value);
        const message = `A Resource is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveResources();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      resources,
      handleSyncList,
      isFetching,
      retrieveResources,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeResource,
    };
  },
});
