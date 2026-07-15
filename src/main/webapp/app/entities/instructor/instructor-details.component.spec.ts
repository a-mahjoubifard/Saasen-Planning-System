import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import InstructorDetails from './instructor-details.vue';
import InstructorService from './instructor.service';

type InstructorDetailsComponentType = InstanceType<typeof InstructorDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instructorSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Instructor Management Detail Component', () => {
    let instructorServiceStub: SinonStubbedInstance<InstructorService>;
    let mountOptions: MountingOptions<InstructorDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      instructorServiceStub = sinon.createStubInstance<InstructorService>(InstructorService);

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
          instructorService: () => instructorServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instructorServiceStub.find.resolves(instructorSample);
        route = {
          params: {
            instructorId: `${123}`,
          },
        };
        const wrapper = shallowMount(InstructorDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.instructor).toMatchObject(instructorSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instructorServiceStub.find.resolves(instructorSample);
        const wrapper = shallowMount(InstructorDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
