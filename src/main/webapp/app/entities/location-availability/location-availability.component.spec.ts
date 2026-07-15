import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import LocationAvailabilityService from './location-availability.service';
import LocationAvailability from './location-availability.vue';

type LocationAvailabilityComponentType = InstanceType<typeof LocationAvailability>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('LocationAvailability Management Component', () => {
    let locationAvailabilityServiceStub: SinonStubbedInstance<LocationAvailabilityService>;
    let mountOptions: MountingOptions<LocationAvailabilityComponentType>['global'];

    beforeEach(() => {
      locationAvailabilityServiceStub = sinon.createStubInstance<LocationAvailabilityService>(LocationAvailabilityService);
      locationAvailabilityServiceStub.retrieve.resolves({ headers: {} });

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
          locationAvailabilityService: () => locationAvailabilityServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        locationAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(LocationAvailability, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(locationAvailabilityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.locationAvailabilities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: LocationAvailabilityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(LocationAvailability, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        locationAvailabilityServiceStub.retrieve.reset();
        locationAvailabilityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        locationAvailabilityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeLocationAvailability();
        await comp.$nextTick(); // clear components

        // THEN
        expect(locationAvailabilityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(locationAvailabilityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
