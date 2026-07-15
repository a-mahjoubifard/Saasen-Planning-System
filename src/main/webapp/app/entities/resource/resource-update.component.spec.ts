import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ResourceUpdate from './resource-update.vue';
import ResourceService from './resource.service';

type ResourceUpdateComponentType = InstanceType<typeof ResourceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const resourceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ResourceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Resource Management Update Component', () => {
    let comp: ResourceUpdateComponentType;
    let resourceServiceStub: SinonStubbedInstance<ResourceService>;

    beforeEach(() => {
      route = {};
      resourceServiceStub = sinon.createStubInstance<ResourceService>(ResourceService);
      resourceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          resourceService: () => resourceServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.resource = resourceSample;
        resourceServiceStub.update.resolves(resourceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(resourceServiceStub.update.calledWith(resourceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        resourceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.resource = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(resourceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        resourceServiceStub.find.resolves(resourceSample);
        resourceServiceStub.retrieve.resolves([resourceSample]);

        // WHEN
        route = {
          params: {
            resourceId: `${resourceSample.id}`,
          },
        };
        const wrapper = shallowMount(ResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.resource).toMatchObject(resourceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        resourceServiceStub.find.resolves(resourceSample);
        const wrapper = shallowMount(ResourceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
