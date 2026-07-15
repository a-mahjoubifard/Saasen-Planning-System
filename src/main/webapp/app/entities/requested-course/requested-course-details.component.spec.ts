import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import RequestedCourseDetails from './requested-course-details.vue';
import RequestedCourseService from './requested-course.service';

type RequestedCourseDetailsComponentType = InstanceType<typeof RequestedCourseDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const requestedCourseSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('RequestedCourse Management Detail Component', () => {
    let requestedCourseServiceStub: SinonStubbedInstance<RequestedCourseService>;
    let mountOptions: MountingOptions<RequestedCourseDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      requestedCourseServiceStub = sinon.createStubInstance<RequestedCourseService>(RequestedCourseService);

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
          requestedCourseService: () => requestedCourseServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        requestedCourseServiceStub.find.resolves(requestedCourseSample);
        route = {
          params: {
            requestedCourseId: `${123}`,
          },
        };
        const wrapper = shallowMount(RequestedCourseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.requestedCourse).toMatchObject(requestedCourseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        requestedCourseServiceStub.find.resolves(requestedCourseSample);
        const wrapper = shallowMount(RequestedCourseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
