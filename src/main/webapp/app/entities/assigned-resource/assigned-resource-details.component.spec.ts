import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import AssignedResourceDetails from './assigned-resource-details.vue';
import AssignedResourceService from './assigned-resource.service';

type AssignedResourceDetailsComponentType = InstanceType<typeof AssignedResourceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const assignedResourceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('AssignedResource Management Detail Component', () => {
    let assignedResourceServiceStub: SinonStubbedInstance<AssignedResourceService>;
    let mountOptions: MountingOptions<AssignedResourceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      assignedResourceServiceStub = sinon.createStubInstance<AssignedResourceService>(AssignedResourceService);

      alertService = new AlertService({
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          assignedResourceService: () => assignedResourceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        assignedResourceServiceStub.find.resolves(assignedResourceSample);
        route = {
          params: {
            assignedResourceId: `${123}`,
          },
        };
        const wrapper = shallowMount(AssignedResourceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.assignedResource).toMatchObject(assignedResourceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        assignedResourceServiceStub.find.resolves(assignedResourceSample);
        const wrapper = shallowMount(AssignedResourceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
