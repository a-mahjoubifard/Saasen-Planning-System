import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import FreelancerService from '@/entities/freelancer/freelancer.service';
import InstructorAvailabilityService from '@/entities/instructor-availability/instructor-availability.service';
import LocationAvailabilityService from '@/entities/location-availability/location-availability.service';
import RequestedCourseService from '@/entities/requested-course/requested-course.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { RequestStatus } from '@/shared/model/enumerations/request-status.model';
import { type IFreelancer } from '@/shared/model/freelancer.model';
import { type IInstructorAvailability } from '@/shared/model/instructor-availability.model';
import { type ILocationAvailability } from '@/shared/model/location-availability.model';
import { type IRequestedCourse } from '@/shared/model/requested-course.model';
import { type ISession, Session } from '@/shared/model/session.model';

import SessionService from './session.service';

export default defineComponent({
  name: 'SessionUpdate',
  setup() {
    const sessionService = inject('sessionService', () => new SessionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const session: Ref<ISession> = ref(new Session());

    const instructorAvailabilityService = inject('instructorAvailabilityService', () => new InstructorAvailabilityService());

    const instructorAvailabilities: Ref<IInstructorAvailability[]> = ref([]);

    const locationAvailabilityService = inject('locationAvailabilityService', () => new LocationAvailabilityService());

    const locationAvailabilities: Ref<ILocationAvailability[]> = ref([]);

    const requestedCourseService = inject('requestedCourseService', () => new RequestedCourseService());

    const requestedCourses: Ref<IRequestedCourse[]> = ref([]);

    const freelancerService = inject('freelancerService', () => new FreelancerService());

    const freelancers: Ref<IFreelancer[]> = ref([]);
    const requestStatusValues: Ref<string[]> = ref(Object.keys(RequestStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSession = async sessionId => {
      try {
        const res = await sessionService().find(sessionId);
        res.startDate = new Date(res.startDate);
        res.endDate = new Date(res.endDate);
        session.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.sessionId) {
      retrieveSession(route.params.sessionId);
    }

    const initRelationships = () => {
      instructorAvailabilityService()
        .retrieve()
        .then(res => {
          instructorAvailabilities.value = res.data;
        });
      locationAvailabilityService()
        .retrieve()
        .then(res => {
          locationAvailabilities.value = res.data;
        });
      requestedCourseService()
        .retrieve()
        .then(res => {
          requestedCourses.value = res.data;
        });
      freelancerService()
        .retrieve()
        .then(res => {
          freelancers.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      startDate: {},
      endDate: {},
      status: {},
      instructorAvailability: {},
      locationAvailability: {},
      requestedCourse: {},
      freelancer: {},
    };
    const v$ = useVuelidate(validationRules, session as any);
    v$.value.$validate();

    return {
      sessionService,
      alertService,
      session,
      previousState,
      requestStatusValues,
      isSaving,
      currentLanguage,
      instructorAvailabilities,
      locationAvailabilities,
      requestedCourses,
      freelancers,
      v$,
      ...useDateFormat({ entityRef: session }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.session.id) {
        this.sessionService()
          .update(this.session)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Session is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.sessionService()
          .create(this.session)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Session is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
