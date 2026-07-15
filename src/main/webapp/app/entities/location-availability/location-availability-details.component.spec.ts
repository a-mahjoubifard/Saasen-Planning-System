import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import LocationAvailabilityDetails from './location-availability-details.vue';
import LocationAvailabilityService from './location-availability.service';

type LocationAvailabilityDetailsComponentType = InstanceType<typeof LocationAvailabilityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const locationAvailabilitySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('LocationAvailability Management Detail Component', () => {
    let locationAvailabilityServiceStub: SinonStubbedInstance<LocationAvailabilityService>;
    let mountOptions: MountingOptions<LocationAvailabilityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      locationAvailabilityServiceStub = sinon.createStubInstance<LocationAvailabilityService>(LocationAvailabilityService);

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
          locationAvailabilityService: () => locationAvailabilityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        locationAvailabilityServiceStub.find.resolves(locationAvailabilitySample);
        route = {
          params: {
            locationAvailabilityId: `${123}`,
          },
        };
        const wrapper = shallowMount(LocationAvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.locationAvailability).toMatchObject(locationAvailabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        locationAvailabilityServiceStub.find.resolves(locationAvailabilitySample);
        const wrapper = shallowMount(LocationAvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
