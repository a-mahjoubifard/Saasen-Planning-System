import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import FreelancerService from '@/entities/freelancer/freelancer.service';
import InstructorAvailabilityService from '@/entities/instructor-availability/instructor-availability.service';
import LocationAvailabilityService from '@/entities/location-availability/location-availability.service';
import RequestedCourseService from '@/entities/requested-course/requested-course.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import SessionUpdate from './session-update.vue';
import SessionService from './session.service';

type SessionUpdateComponentType = InstanceType<typeof SessionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const sessionSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<SessionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Session Management Update Component', () => {
    let comp: SessionUpdateComponentType;
    let sessionServiceStub: SinonStubbedInstance<SessionService>;

    beforeEach(() => {
      route = {};
      sessionServiceStub = sinon.createStubInstance<SessionService>(SessionService);
      sessionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          sessionService: () => sessionServiceStub,
          instructorAvailabilityService: () =>
            sinon.createStubInstance<InstructorAvailabilityService>(InstructorAvailabilityService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          locationAvailabilityService: () =>
            sinon.createStubInstance<LocationAvailabilityService>(LocationAvailabilityService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          requestedCourseService: () =>
            sinon.createStubInstance<RequestedCourseService>(RequestedCourseService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          freelancerService: () =>
            sinon.createStubInstance<FreelancerService>(FreelancerService, {
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
        const wrapper = shallowMount(SessionUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(SessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.session = sessionSample;
        sessionServiceStub.update.resolves(sessionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(sessionServiceStub.update.calledWith(sessionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        sessionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(SessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.session = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(sessionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        sessionServiceStub.find.resolves(sessionSample);
        sessionServiceStub.retrieve.resolves([sessionSample]);

        // WHEN
        route = {
          params: {
            sessionId: `${sessionSample.id}`,
          },
        };
        const wrapper = shallowMount(SessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.session).toMatchObject(sessionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        sessionServiceStub.find.resolves(sessionSample);
        const wrapper = shallowMount(SessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
