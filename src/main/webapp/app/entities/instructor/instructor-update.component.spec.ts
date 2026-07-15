import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import InstructorUpdate from './instructor-update.vue';
import InstructorService from './instructor.service';

type InstructorUpdateComponentType = InstanceType<typeof InstructorUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instructorSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InstructorUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Instructor Management Update Component', () => {
    let comp: InstructorUpdateComponentType;
    let instructorServiceStub: SinonStubbedInstance<InstructorService>;

    beforeEach(() => {
      route = {};
      instructorServiceStub = sinon.createStubInstance<InstructorService>(InstructorService);
      instructorServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          instructorService: () => instructorServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(InstructorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instructor = instructorSample;
        instructorServiceStub.update.resolves(instructorSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructorServiceStub.update.calledWith(instructorSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        instructorServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InstructorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instructor = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructorServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        instructorServiceStub.find.resolves(instructorSample);
        instructorServiceStub.retrieve.resolves([instructorSample]);

        // WHEN
        route = {
          params: {
            instructorId: `${instructorSample.id}`,
          },
        };
        const wrapper = shallowMount(InstructorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.instructor).toMatchObject(instructorSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instructorServiceStub.find.resolves(instructorSample);
        const wrapper = shallowMount(InstructorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
