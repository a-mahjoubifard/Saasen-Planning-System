import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import RequestedCourseService from './requested-course.service';
import RequestedCourse from './requested-course.vue';

type RequestedCourseComponentType = InstanceType<typeof RequestedCourse>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('RequestedCourse Management Component', () => {
    let requestedCourseServiceStub: SinonStubbedInstance<RequestedCourseService>;
    let mountOptions: MountingOptions<RequestedCourseComponentType>['global'];

    beforeEach(() => {
      requestedCourseServiceStub = sinon.createStubInstance<RequestedCourseService>(RequestedCourseService);
      requestedCourseServiceStub.retrieve.resolves({ headers: {} });

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
          requestedCourseService: () => requestedCourseServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        requestedCourseServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(RequestedCourse, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(requestedCourseServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.requestedCourses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: RequestedCourseComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(RequestedCourse, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        requestedCourseServiceStub.retrieve.reset();
        requestedCourseServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        requestedCourseServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeRequestedCourse();
        await comp.$nextTick(); // clear components

        // THEN
        expect(requestedCourseServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(requestedCourseServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
