import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InstructorService from '@/entities/instructor/instructor.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import InstructorAvailabilityUpdate from './instructor-availability-update.vue';
import InstructorAvailabilityService from './instructor-availability.service';

type InstructorAvailabilityUpdateComponentType = InstanceType<typeof InstructorAvailabilityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instructorAvailabilitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InstructorAvailabilityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InstructorAvailability Management Update Component', () => {
    let comp: InstructorAvailabilityUpdateComponentType;
    let instructorAvailabilityServiceStub: SinonStubbedInstance<InstructorAvailabilityService>;

    beforeEach(() => {
      route = {};
      instructorAvailabilityServiceStub = sinon.createStubInstance<InstructorAvailabilityService>(InstructorAvailabilityService);
      instructorAvailabilityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          instructorAvailabilityService: () => instructorAvailabilityServiceStub,
          instructorService: () =>
            sinon.createStubInstance<InstructorService>(InstructorService, {
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
        const wrapper = shallowMount(InstructorAvailabilityUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InstructorAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instructorAvailability = instructorAvailabilitySample;
        instructorAvailabilityServiceStub.update.resolves(instructorAvailabilitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructorAvailabilityServiceStub.update.calledWith(instructorAvailabilitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        instructorAvailabilityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InstructorAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instructorAvailability = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructorAvailabilityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        instructorAvailabilityServiceStub.find.resolves(instructorAvailabilitySample);
        instructorAvailabilityServiceStub.retrieve.resolves([instructorAvailabilitySample]);

        // WHEN
        route = {
          params: {
            instructorAvailabilityId: `${instructorAvailabilitySample.id}`,
          },
        };
        const wrapper = shallowMount(InstructorAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.instructorAvailability).toMatchObject(instructorAvailabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instructorAvailabilityServiceStub.find.resolves(instructorAvailabilitySample);
        const wrapper = shallowMount(InstructorAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
