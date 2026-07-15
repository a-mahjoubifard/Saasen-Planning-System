import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import FreelancerAssignmentRequestService from './freelancer-assignment-request.service';
import FreelancerAssignmentRequest from './freelancer-assignment-request.vue';

type FreelancerAssignmentRequestComponentType = InstanceType<typeof FreelancerAssignmentRequest>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('FreelancerAssignmentRequest Management Component', () => {
    let freelancerAssignmentRequestServiceStub: SinonStubbedInstance<FreelancerAssignmentRequestService>;
    let mountOptions: MountingOptions<FreelancerAssignmentRequestComponentType>['global'];

    beforeEach(() => {
      freelancerAssignmentRequestServiceStub =
        sinon.createStubInstance<FreelancerAssignmentRequestService>(FreelancerAssignmentRequestService);
      freelancerAssignmentRequestServiceStub.retrieve.resolves({ headers: {} });

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
          freelancerAssignmentRequestService: () => freelancerAssignmentRequestServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        freelancerAssignmentRequestServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(FreelancerAssignmentRequest, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(freelancerAssignmentRequestServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.freelancerAssignmentRequests[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: FreelancerAssignmentRequestComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(FreelancerAssignmentRequest, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        freelancerAssignmentRequestServiceStub.retrieve.reset();
        freelancerAssignmentRequestServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        freelancerAssignmentRequestServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeFreelancerAssignmentRequest();
        await comp.$nextTick(); // clear components

        // THEN
        expect(freelancerAssignmentRequestServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(freelancerAssignmentRequestServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
