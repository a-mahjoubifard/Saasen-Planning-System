import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import LocationService from '@/entities/location/location.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import LocationAvailabilityUpdate from './location-availability-update.vue';
import LocationAvailabilityService from './location-availability.service';

type LocationAvailabilityUpdateComponentType = InstanceType<typeof LocationAvailabilityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const locationAvailabilitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<LocationAvailabilityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('LocationAvailability Management Update Component', () => {
    let comp: LocationAvailabilityUpdateComponentType;
    let locationAvailabilityServiceStub: SinonStubbedInstance<LocationAvailabilityService>;

    beforeEach(() => {
      route = {};
      locationAvailabilityServiceStub = sinon.createStubInstance<LocationAvailabilityService>(LocationAvailabilityService);
      locationAvailabilityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          locationAvailabilityService: () => locationAvailabilityServiceStub,
          locationService: () =>
            sinon.createStubInstance<LocationService>(LocationService, {
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
        const wrapper = shallowMount(LocationAvailabilityUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(LocationAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.locationAvailability = locationAvailabilitySample;
        locationAvailabilityServiceStub.update.resolves(locationAvailabilitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(locationAvailabilityServiceStub.update.calledWith(locationAvailabilitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        locationAvailabilityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(LocationAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.locationAvailability = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(locationAvailabilityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        locationAvailabilityServiceStub.find.resolves(locationAvailabilitySample);
        locationAvailabilityServiceStub.retrieve.resolves([locationAvailabilitySample]);

        // WHEN
        route = {
          params: {
            locationAvailabilityId: `${locationAvailabilitySample.id}`,
          },
        };
        const wrapper = shallowMount(LocationAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.locationAvailability).toMatchObject(locationAvailabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        locationAvailabilityServiceStub.find.resolves(locationAvailabilitySample);
        const wrapper = shallowMount(LocationAvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
