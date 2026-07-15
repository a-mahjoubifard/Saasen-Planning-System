import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CourseService from './course.service';
import Course from './course.vue';

type CourseComponentType = InstanceType<typeof Course>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Course Management Component', () => {
    let courseServiceStub: SinonStubbedInstance<CourseService>;
    let mountOptions: MountingOptions<CourseComponentType>['global'];

    beforeEach(() => {
      courseServiceStub = sinon.createStubInstance<CourseService>(CourseService);
      courseServiceStub.retrieve.resolves({ headers: {} });

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
          courseService: () => courseServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Course, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(courseServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.courses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CourseComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Course, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        courseServiceStub.retrieve.reset();
        courseServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        courseServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCourse();
        await comp.$nextTick(); // clear components

        // THEN
        expect(courseServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(courseServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
