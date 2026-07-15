import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IResourceAvailability } from '@/shared/model/resource-availability.model';

import ResourceAvailabilityService from './resource-availability.service';

export default defineComponent({
  name: 'ResourceAvailability',
  setup() {
    const dateFormat = useDateFormat();
    const resourceAvailabilityService = inject('resourceAvailabilityService', () => new ResourceAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const resourceAvailabilities: Ref<IResourceAvailability[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveResourceAvailabilitys = async () => {
      isFetching.value = true;
      try {
        const res = await resourceAvailabilityService().retrieve();
        resourceAvailabilities.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveResourceAvailabilitys();
    };

    onMounted(async () => {
      await retrieveResourceAvailabilitys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IResourceAvailability) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeResourceAvailability = async () => {
      try {
        await resourceAvailabilityService().delete(removeId.value);
        const message = `A ResourceAvailability is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveResourceAvailabilitys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      resourceAvailabilities,
      handleSyncList,
      isFetching,
      retrieveResourceAvailabilitys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeResourceAvailability,
    };
  },
});
