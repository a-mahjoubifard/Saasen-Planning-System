import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import AssignedResourceService from './assigned-resource.service';
import AssignedResource from './assigned-resource.vue';

type AssignedResourceComponentType = InstanceType<typeof AssignedResource>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('AssignedResource Management Component', () => {
    let assignedResourceServiceStub: SinonStubbedInstance<AssignedResourceService>;
    let mountOptions: MountingOptions<AssignedResourceComponentType>['global'];

    beforeEach(() => {
      assignedResourceServiceStub = sinon.createStubInstance<AssignedResourceService>(AssignedResourceService);
      assignedResourceServiceStub.retrieve.resolves({ headers: {} });

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
          assignedResourceService: () => assignedResourceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        assignedResourceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(AssignedResource, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(assignedResourceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.assignedResources[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: AssignedResourceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(AssignedResource, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        assignedResourceServiceStub.retrieve.reset();
        assignedResourceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        assignedResourceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeAssignedResource();
        await comp.$nextTick(); // clear components

        // THEN
        expect(assignedResourceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(assignedResourceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
