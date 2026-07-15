import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IAssignedResource } from '@/shared/model/assigned-resource.model';

import AssignedResourceService from './assigned-resource.service';

export default defineComponent({
  name: 'AssignedResource',
  setup() {
    const assignedResourceService = inject('assignedResourceService', () => new AssignedResourceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const assignedResources: Ref<IAssignedResource[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveAssignedResources = async () => {
      isFetching.value = true;
      try {
        const res = await assignedResourceService().retrieve();
        assignedResources.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveAssignedResources();
    };

    onMounted(async () => {
      await retrieveAssignedResources();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IAssignedResource) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeAssignedResource = async () => {
      try {
        await assignedResourceService().delete(removeId.value);
        const message = `A AssignedResource is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveAssignedResources();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      assignedResources,
      handleSyncList,
      isFetching,
      retrieveAssignedResources,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeAssignedResource,
    };
  },
});
