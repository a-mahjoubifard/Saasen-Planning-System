import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import InstructorAvailabilityService from './instructor-availability.service';
import InstructorAvailability from './instructor-availability.vue';

type InstructorAvailabilityComponentType = InstanceType<typeof InstructorAvailability>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InstructorAvailability Management Component', () => {
    let instructorAvailabilityServiceStub: SinonStubbedInstance<InstructorAvailabilityService>;
    let mountOptions: MountingOptions<InstructorAvailabilityComponentType>['global'];

    beforeEach(() => {
      instructorAvailabilityServiceStub = sinon.createStubInstance<InstructorAvailabilityService>(InstructorAvailabilityService);
      instructorAvailabilityServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          instructorAvailabilityService: () => instructorAvailabilityServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instructorAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(InstructorAvailability, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(instructorAvailabilityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.instructorAvailabilities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: InstructorAvailabilityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InstructorAvailability, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        instructorAvailabilityServiceStub.retrieve.reset();
        instructorAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        instructorAvailabilityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInstructorAvailability();
        await comp.$nextTick(); // clear components

        // THEN
        expect(instructorAvailabilityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(instructorAvailabilityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
