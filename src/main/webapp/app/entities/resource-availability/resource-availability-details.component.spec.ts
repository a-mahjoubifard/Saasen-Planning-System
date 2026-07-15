import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ResourceAvailabilityDetails from './resource-availability-details.vue';
import ResourceAvailabilityService from './resource-availability.service';

type ResourceAvailabilityDetailsComponentType = InstanceType<typeof ResourceAvailabilityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const resourceAvailabilitySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ResourceAvailability Management Detail Component', () => {
    let resourceAvailabilityServiceStub: SinonStubbedInstance<ResourceAvailabilityService>;
    let mountOptions: MountingOptions<ResourceAvailabilityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      resourceAvailabilityServiceStub = sinon.createStubInstance<ResourceAvailabilityService>(ResourceAvailabilityService);

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
          resourceAvailabilityService: () => resourceAvailabilityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        resourceAvailabilityServiceStub.find.resolves(resourceAvailabilitySample);
        route = {
          params: {
            resourceAvailabilityId: `${123}`,
          },
        };
        const wrapper = shallowMount(ResourceAvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.resourceAvailability).toMatchObject(resourceAvailabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        resourceAvailabilityServiceStub.find.resolves(resourceAvailabilitySample);
        const wrapper = shallowMount(ResourceAvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
