import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ILocationAvailability } from '@/shared/model/location-availability.model';

import LocationAvailabilityService from './location-availability.service';

export default defineComponent({
  name: 'LocationAvailability',
  setup() {
    const dateFormat = useDateFormat();
    const locationAvailabilityService = inject('locationAvailabilityService', () => new LocationAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const locationAvailabilities: Ref<ILocationAvailability[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveLocationAvailabilitys = async () => {
      isFetching.value = true;
      try {
        const res = await locationAvailabilityService().retrieve();
        locationAvailabilities.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveLocationAvailabilitys();
    };

    onMounted(async () => {
      await retrieveLocationAvailabilitys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ILocationAvailability) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeLocationAvailability = async () => {
      try {
        await locationAvailabilityService().delete(removeId.value);
        const message = `A LocationAvailability is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveLocationAvailabilitys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      locationAvailabilities,
      handleSyncList,
      isFetching,
      retrieveLocationAvailabilitys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeLocationAvailability,
    };
  },
});
