import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import SessionDetails from './session-details.vue';
import SessionService from './session.service';

type SessionDetailsComponentType = InstanceType<typeof SessionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const sessionSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Session Management Detail Component', () => {
    let sessionServiceStub: SinonStubbedInstance<SessionService>;
    let mountOptions: MountingOptions<SessionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      sessionServiceStub = sinon.createStubInstance<SessionService>(SessionService);

      alertService = new AlertService({
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          sessionService: () => sessionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        sessionServiceStub.find.resolves(sessionSample);
        route = {
          params: {
            sessionId: `${123}`,
          },
        };
        const wrapper = shallowMount(SessionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.session).toMatchObject(sessionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        sessionServiceStub.find.resolves(sessionSample);
        const wrapper = shallowMount(SessionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
