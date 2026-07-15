import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import FreelancerAssignmentRequestDetails from './freelancer-assignment-request-details.vue';
import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';

type FreelancerAssignmentRequestDetailsComponentType = InstanceType<typeof FreelancerAssignmentRequestDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const freelancerAssignmentRequestSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('FreelancerAssignmentRequest Management Detail Component', () => {
    let freelancerAssignmentRequestServiceStub: SinonStubbedInstance<FreelancerAssignmentRequestService>;
    let mountOptions: MountingOptions<FreelancerAssignmentRequestDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      freelancerAssignmentRequestServiceStub =
        sinon.createStubInstance<FreelancerAssignmentRequestService>(FreelancerAssignmentRequestService);

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
          freelancerAssignmentRequestService: () => freelancerAssignmentRequestServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        freelancerAssignmentRequestServiceStub.find.resolves(freelancerAssignmentRequestSample);
        route = {
          params: {
            freelancerAssignmentRequestId: `${123}`,
          },
        };
        const wrapper = shallowMount(FreelancerAssignmentRequestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.freelancerAssignmentRequest).toMatchObject(freelancerAssignmentRequestSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        freelancerAssignmentRequestServiceStub.find.resolves(freelancerAssignmentRequestSample);
        const wrapper = shallowMount(FreelancerAssignmentRequestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
