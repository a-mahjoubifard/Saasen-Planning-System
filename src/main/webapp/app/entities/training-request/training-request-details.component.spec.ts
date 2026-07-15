import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import TrainingRequestDetails from './training-request-details.vue';
import TrainingRequestService from './training-request.service';

type TrainingRequestDetailsComponentType = InstanceType<typeof TrainingRequestDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trainingRequestSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrainingRequest Management Detail Component', () => {
    let trainingRequestServiceStub: SinonStubbedInstance<TrainingRequestService>;
    let mountOptions: MountingOptions<TrainingRequestDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trainingRequestServiceStub = sinon.createStubInstance<TrainingRequestService>(TrainingRequestService);

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
          trainingRequestService: () => trainingRequestServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trainingRequestServiceStub.find.resolves(trainingRequestSample);
        route = {
          params: {
            trainingRequestId: `${123}`,
          },
        };
        const wrapper = shallowMount(TrainingRequestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trainingRequest).toMatchObject(trainingRequestSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trainingRequestServiceStub.find.resolves(trainingRequestSample);
        const wrapper = shallowMount(TrainingRequestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
