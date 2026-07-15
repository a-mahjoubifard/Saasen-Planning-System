import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import FreelancerService from '@/entities/freelancer/freelancer.service';
import SessionService from '@/entities/session/session.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import FreelancerAssignmentRequestUpdate from './freelancer-assignment-request-update.vue';
import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';

type FreelancerAssignmentRequestUpdateComponentType = InstanceType<typeof FreelancerAssignmentRequestUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const freelancerAssignmentRequestSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FreelancerAssignmentRequestUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('FreelancerAssignmentRequest Management Update Component', () => {
    let comp: FreelancerAssignmentRequestUpdateComponentType;
    let freelancerAssignmentRequestServiceStub: SinonStubbedInstance<FreelancerAssignmentRequestService>;

    beforeEach(() => {
      route = {};
      freelancerAssignmentRequestServiceStub =
        sinon.createStubInstance<FreelancerAssignmentRequestService>(FreelancerAssignmentRequestService);
      freelancerAssignmentRequestServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          freelancerAssignmentRequestService: () => freelancerAssignmentRequestServiceStub,
          freelancerService: () =>
            sinon.createStubInstance<FreelancerService>(FreelancerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          sessionService: () =>
            sinon.createStubInstance<SessionService>(SessionService, {
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
        const wrapper = shallowMount(FreelancerAssignmentRequestUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(FreelancerAssignmentRequestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.freelancerAssignmentRequest = freelancerAssignmentRequestSample;
        freelancerAssignmentRequestServiceStub.update.resolves(freelancerAssignmentRequestSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(freelancerAssignmentRequestServiceStub.update.calledWith(freelancerAssignmentRequestSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        freelancerAssignmentRequestServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FreelancerAssignmentRequestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.freelancerAssignmentRequest = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(freelancerAssignmentRequestServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        freelancerAssignmentRequestServiceStub.find.resolves(freelancerAssignmentRequestSample);
        freelancerAssignmentRequestServiceStub.retrieve.resolves([freelancerAssignmentRequestSample]);

        // WHEN
        route = {
          params: {
            freelancerAssignmentRequestId: `${freelancerAssignmentRequestSample.id}`,
          },
        };
        const wrapper = shallowMount(FreelancerAssignmentRequestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.freelancerAssignmentRequest).toMatchObject(freelancerAssignmentRequestSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        freelancerAssignmentRequestServiceStub.find.resolves(freelancerAssignmentRequestSample);
        const wrapper = shallowMount(FreelancerAssignmentRequestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
