import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ResourceAvailabilityService from '@/entities/resource-availability/resource-availability.service';
import SessionService from '@/entities/session/session.service';
import AlertService from '@/shared/alert/alert.service';

import AssignedResourceUpdate from './assigned-resource-update.vue';
import AssignedResourceService from './assigned-resource.service';

type AssignedResourceUpdateComponentType = InstanceType<typeof AssignedResourceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const assignedResourceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AssignedResourceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('AssignedResource Management Update Component', () => {
    let comp: AssignedResourceUpdateComponentType;
    let assignedResourceServiceStub: SinonStubbedInstance<AssignedResourceService>;

    beforeEach(() => {
      route = {};
      assignedResourceServiceStub = sinon.createStubInstance<AssignedResourceService>(AssignedResourceService);
      assignedResourceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          assignedResourceService: () => assignedResourceServiceStub,
          resourceAvailabilityService: () =>
            sinon.createStubInstance<ResourceAvailabilityService>(ResourceAvailabilityService, {
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

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(AssignedResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.assignedResource = assignedResourceSample;
        assignedResourceServiceStub.update.resolves(assignedResourceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(assignedResourceServiceStub.update.calledWith(assignedResourceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        assignedResourceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AssignedResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.assignedResource = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(assignedResourceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        assignedResourceServiceStub.find.resolves(assignedResourceSample);
        assignedResourceServiceStub.retrieve.resolves([assignedResourceSample]);

        // WHEN
        route = {
          params: {
            assignedResourceId: `${assignedResourceSample.id}`,
          },
        };
        const wrapper = shallowMount(AssignedResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.assignedResource).toMatchObject(assignedResourceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        assignedResourceServiceStub.find.resolves(assignedResourceSample);
        const wrapper = shallowMount(AssignedResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
