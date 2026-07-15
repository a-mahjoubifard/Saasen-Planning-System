import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CourseService from '@/entities/course/course.service';
import TrainingRequestService from '@/entities/training-request/training-request.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import RequestedCourseUpdate from './requested-course-update.vue';
import RequestedCourseService from './requested-course.service';

type RequestedCourseUpdateComponentType = InstanceType<typeof RequestedCourseUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const requestedCourseSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<RequestedCourseUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('RequestedCourse Management Update Component', () => {
    let comp: RequestedCourseUpdateComponentType;
    let requestedCourseServiceStub: SinonStubbedInstance<RequestedCourseService>;

    beforeEach(() => {
      route = {};
      requestedCourseServiceStub = sinon.createStubInstance<RequestedCourseService>(RequestedCourseService);
      requestedCourseServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          requestedCourseService: () => requestedCourseServiceStub,
          trainingRequestService: () =>
            sinon.createStubInstance<TrainingRequestService>(TrainingRequestService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          courseService: () =>
            sinon.createStubInstance<CourseService>(CourseService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(RequestedCourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(RequestedCourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.requestedCourse = requestedCourseSample;
        requestedCourseServiceStub.update.resolves(requestedCourseSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(requestedCourseServiceStub.update.calledWith(requestedCourseSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        requestedCourseServiceStub.create.resolves(entity);
        const wrapper = shallowMount(RequestedCourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.requestedCourse = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(requestedCourseServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        requestedCourseServiceStub.find.resolves(requestedCourseSample);
        requestedCourseServiceStub.retrieve.resolves([requestedCourseSample]);

        // WHEN
        route = {
          params: {
            requestedCourseId: `${requestedCourseSample.id}`,
          },
        };
        const wrapper = shallowMount(RequestedCourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.requestedCourse).toMatchObject(requestedCourseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        requestedCourseServiceStub.find.resolves(requestedCourseSample);
        const wrapper = shallowMount(RequestedCourseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
