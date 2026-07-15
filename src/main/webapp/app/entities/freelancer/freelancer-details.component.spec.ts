import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import FreelancerDetails from './freelancer-details.vue';
import FreelancerService from './freelancer.service';

type FreelancerDetailsComponentType = InstanceType<typeof FreelancerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const freelancerSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Freelancer Management Detail Component', () => {
    let freelancerServiceStub: SinonStubbedInstance<FreelancerService>;
    let mountOptions: MountingOptions<FreelancerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      freelancerServiceStub = sinon.createStubInstance<FreelancerService>(FreelancerService);

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
          freelancerService: () => freelancerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        freelancerServiceStub.find.resolves(freelancerSample);
        route = {
          params: {
            freelancerId: `${123}`,
          },
        };
        const wrapper = shallowMount(FreelancerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.freelancer).toMatchObject(freelancerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        freelancerServiceStub.find.resolves(freelancerSample);
        const wrapper = shallowMount(FreelancerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
