import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import FreelancerUpdate from './freelancer-update.vue';
import FreelancerService from './freelancer.service';

type FreelancerUpdateComponentType = InstanceType<typeof FreelancerUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const freelancerSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FreelancerUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Freelancer Management Update Component', () => {
    let comp: FreelancerUpdateComponentType;
    let freelancerServiceStub: SinonStubbedInstance<FreelancerService>;

    beforeEach(() => {
      route = {};
      freelancerServiceStub = sinon.createStubInstance<FreelancerService>(FreelancerService);
      freelancerServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          freelancerService: () => freelancerServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(FreelancerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.freelancer = freelancerSample;
        freelancerServiceStub.update.resolves(freelancerSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(freelancerServiceStub.update.calledWith(freelancerSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        freelancerServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FreelancerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.freelancer = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(freelancerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        freelancerServiceStub.find.resolves(freelancerSample);
        freelancerServiceStub.retrieve.resolves([freelancerSample]);

        // WHEN
        route = {
          params: {
            freelancerId: `${freelancerSample.id}`,
          },
        };
        const wrapper = shallowMount(FreelancerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.freelancer).toMatchObject(freelancerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        freelancerServiceStub.find.resolves(freelancerSample);
        const wrapper = shallowMount(FreelancerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
