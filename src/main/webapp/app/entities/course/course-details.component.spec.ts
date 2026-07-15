import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CourseDetails from './course-details.vue';
import CourseService from './course.service';

type CourseDetailsComponentType = InstanceType<typeof CourseDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Course Management Detail Component', () => {
    let courseServiceStub: SinonStubbedInstance<CourseService>;
    let mountOptions: MountingOptions<CourseDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      courseServiceStub = sinon.createStubInstance<CourseService>(CourseService);

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
          courseService: () => courseServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseServiceStub.find.resolves(courseSample);
        route = {
          params: {
            courseId: `${123}`,
          },
        };
        const wrapper = shallowMount(CourseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.course).toMatchObject(courseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseServiceStub.find.resolves(courseSample);
        const wrapper = shallowMount(CourseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
