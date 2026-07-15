import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import TrainingRequestService from './training-request.service';
import TrainingRequest from './training-request.vue';

type TrainingRequestComponentType = InstanceType<typeof TrainingRequest>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrainingRequest Management Component', () => {
    let trainingRequestServiceStub: SinonStubbedInstance<TrainingRequestService>;
    let mountOptions: MountingOptions<TrainingRequestComponentType>['global'];

    beforeEach(() => {
      trainingRequestServiceStub = sinon.createStubInstance<TrainingRequestService>(TrainingRequestService);
      trainingRequestServiceStub.retrieve.resolves({ headers: {} });

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
          trainingRequestService: () => trainingRequestServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trainingRequestServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrainingRequest, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trainingRequestServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trainingRequests[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: TrainingRequestComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrainingRequest, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trainingRequestServiceStub.retrieve.reset();
        trainingRequestServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trainingRequestServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrainingRequest();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trainingRequestServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trainingRequestServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
