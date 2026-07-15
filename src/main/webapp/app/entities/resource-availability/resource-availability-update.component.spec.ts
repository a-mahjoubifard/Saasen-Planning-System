import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ResourceService from '@/entities/resource/resource.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import ResourceAvailabilityUpdate from './resource-availability-update.vue';
import ResourceAvailabilityService from './resource-availability.service';

type ResourceAvailabilityUpdateComponentType = InstanceType<typeof ResourceAvailabilityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const resourceAvailabilitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ResourceAvailabilityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ResourceAvailability Management Update Component', () => {
    let comp: ResourceAvailabilityUpdateComponentType;
    let resourceAvailabilityServiceStub: SinonStubbedInstance<ResourceAvailabilityService>;

    beforeEach(() => {
      route = {};
      resourceAvailabilityServiceStub = sinon.createStubInstance<ResourceAvailabilityService>(ResourceAvailabilityService);
      resourceAvailabilityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          resourceAvailabilityService: () => resourceAvailabilityServiceStub,
          resourceService: () =>
            sinon.createStubInstance<ResourceService>(ResourceService, {
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
        const wrapper = shallowMount(ResourceAvailabilityUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ResourceAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.resourceAvailability = resourceAvailabilitySample;
        resourceAvailabilityServiceStub.update.resolves(resourceAvailabilitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(resourceAvailabilityServiceStub.update.calledWith(resourceAvailabilitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        resourceAvailabilityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ResourceAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.resourceAvailability = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(resourceAvailabilityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        resourceAvailabilityServiceStub.find.resolves(resourceAvailabilitySample);
        resourceAvailabilityServiceStub.retrieve.resolves([resourceAvailabilitySample]);

        // WHEN
        route = {
          params: {
            resourceAvailabilityId: `${resourceAvailabilitySample.id}`,
          },
        };
        const wrapper = shallowMount(ResourceAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.resourceAvailability).toMatchObject(resourceAvailabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        resourceAvailabilityServiceStub.find.resolves(resourceAvailabilitySample);
        const wrapper = shallowMount(ResourceAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
