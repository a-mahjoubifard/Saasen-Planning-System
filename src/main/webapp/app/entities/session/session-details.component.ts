import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type ISession } from '@/shared/model/session.model';

import SessionService from './session.service';

export default defineComponent({
  name: 'SessionDetails',
  setup() {
    const dateFormat = useDateFormat();
    const sessionService = inject('sessionService', () => new SessionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const session: Ref<ISession> = ref({});

    const retrieveSession = async sessionId => {
      try {
        const res = await sessionService().find(sessionId);
        session.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.sessionId) {
      retrieveSession(route.params.sessionId);
    }

    return {
      ...dateFormat,
      alertService,
      session,

      previousState,
    };
  },
});
