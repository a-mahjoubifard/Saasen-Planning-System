import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ResourceAvailabilityService from './resource-availability.service';
import ResourceAvailability from './resource-availability.vue';

type ResourceAvailabilityComponentType = InstanceType<typeof ResourceAvailability>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ResourceAvailability Management Component', () => {
    let resourceAvailabilityServiceStub: SinonStubbedInstance<ResourceAvailabilityService>;
    let mountOptions: MountingOptions<ResourceAvailabilityComponentType>['global'];

    beforeEach(() => {
      resourceAvailabilityServiceStub = sinon.createStubInstance<ResourceAvailabilityService>(ResourceAvailabilityService);
      resourceAvailabilityServiceStub.retrieve.resolves({ headers: {} });

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
          resourceAvailabilityService: () => resourceAvailabilityServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        resourceAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(ResourceAvailability, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(resourceAvailabilityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.resourceAvailabilities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ResourceAvailabilityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ResourceAvailability, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        resourceAvailabilityServiceStub.retrieve.reset();
        resourceAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        resourceAvailabilityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeResourceAvailability();
        await comp.$nextTick(); // clear components

        // THEN
        expect(resourceAvailabilityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(resourceAvailabilityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
