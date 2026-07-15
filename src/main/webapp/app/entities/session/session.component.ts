import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ISession } from '@/shared/model/session.model';

import SessionService from './session.service';

export default defineComponent({
  name: 'Session',
  setup() {
    const dateFormat = useDateFormat();
    const sessionService = inject('sessionService', () => new SessionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const sessions: Ref<ISession[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveSessions = async () => {
      isFetching.value = true;
      try {
        const res = await sessionService().retrieve();
        sessions.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveSessions();
    };

    onMounted(async () => {
      await retrieveSessions();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ISession) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeSession = async () => {
      try {
        await sessionService().delete(removeId.value);
        const message = `A Session is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveSessions();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      sessions,
      handleSyncList,
      isFetching,
      retrieveSessions,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeSession,
    };
  },
});
