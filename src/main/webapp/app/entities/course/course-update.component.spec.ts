import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CourseUpdate from './course-update.vue';
import CourseService from './course.service';

type CourseUpdateComponentType = InstanceType<typeof CourseUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CourseUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Course Management Update Component', () => {
    let comp: CourseUpdateComponentType;
    let courseServiceStub: SinonStubbedInstance<CourseService>;

    beforeEach(() => {
      route = {};
      courseServiceStub = sinon.createStubInstance<CourseService>(CourseService);
      courseServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          courseService: () => courseServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.course = courseSample;
        courseServiceStub.update.resolves(courseSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseServiceStub.update.calledWith(courseSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        courseServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.course = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        courseServiceStub.find.resolves(courseSample);
        courseServiceStub.retrieve.resolves([courseSample]);

        // WHEN
        route = {
          params: {
            courseId: `${courseSample.id}`,
          },
        };
        const wrapper = shallowMount(CourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.course).toMatchObject(courseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseServiceStub.find.resolves(courseSample);
        const wrapper = shallowMount(CourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
