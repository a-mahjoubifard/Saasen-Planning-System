import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import InstructorService from './instructor.service';
import Instructor from './instructor.vue';

type InstructorComponentType = InstanceType<typeof Instructor>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Instructor Management Component', () => {
    let instructorServiceStub: SinonStubbedInstance<InstructorService>;
    let mountOptions: MountingOptions<InstructorComponentType>['global'];

    beforeEach(() => {
      instructorServiceStub = sinon.createStubInstance<InstructorService>(InstructorService);
      instructorServiceStub.retrieve.resolves({ headers: {} });

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
          instructorService: () => instructorServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instructorServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Instructor, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(instructorServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.instructors[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: InstructorComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Instructor, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        instructorServiceStub.retrieve.reset();
        instructorServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        instructorServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInstructor();
        await comp.$nextTick(); // clear components

        // THEN
        expect(instructorServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(instructorServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
