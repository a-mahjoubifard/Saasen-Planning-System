import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import SessionService from './session.service';
import Session from './session.vue';

type SessionComponentType = InstanceType<typeof Session>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Session Management Component', () => {
    let sessionServiceStub: SinonStubbedInstance<SessionService>;
    let mountOptions: MountingOptions<SessionComponentType>['global'];

    beforeEach(() => {
      sessionServiceStub = sinon.createStubInstance<SessionService>(SessionService);
      sessionServiceStub.retrieve.resolves({ headers: {} });

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
          sessionService: () => sessionServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        sessionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Session, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(sessionServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.sessions[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: SessionComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Session, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        sessionServiceStub.retrieve.reset();
        sessionServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        sessionServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeSession();
        await comp.$nextTick(); // clear components

        // THEN
        expect(sessionServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(sessionServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
