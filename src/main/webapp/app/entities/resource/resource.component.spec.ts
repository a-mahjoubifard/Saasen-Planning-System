import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ResourceService from './resource.service';
import Resource from './resource.vue';

type ResourceComponentType = InstanceType<typeof Resource>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Resource Management Component', () => {
    let resourceServiceStub: SinonStubbedInstance<ResourceService>;
    let mountOptions: MountingOptions<ResourceComponentType>['global'];

    beforeEach(() => {
      resourceServiceStub = sinon.createStubInstance<ResourceService>(ResourceService);
      resourceServiceStub.retrieve.resolves({ headers: {} });

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
          resourceService: () => resourceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        resourceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Resource, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(resourceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.resources[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ResourceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Resource, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        resourceServiceStub.retrieve.reset();
        resourceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        resourceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeResource();
        await comp.$nextTick(); // clear components

        // THEN
        expect(resourceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(resourceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
