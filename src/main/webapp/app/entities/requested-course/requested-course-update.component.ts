import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CourseService from '@/entities/course/course.service';
import TrainingRequestService from '@/entities/training-request/training-request.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ICourse } from '@/shared/model/course.model';
import { RequestStatus } from '@/shared/model/enumerations/request-status.model';
import { type IRequestedCourse, RequestedCourse } from '@/shared/model/requested-course.model';
import { type ITrainingRequest } from '@/shared/model/training-request.model';

import RequestedCourseService from './requested-course.service';

export default defineComponent({
  name: 'RequestedCourseUpdate',
  setup() {
    const requestedCourseService = inject('requestedCourseService', () => new RequestedCourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const requestedCourse: Ref<IRequestedCourse> = ref(new RequestedCourse());

    const trainingRequestService = inject('trainingRequestService', () => new TrainingRequestService());

    const trainingRequests: Ref<ITrainingRequest[]> = ref([]);

    const courseService = inject('courseService', () => new CourseService());

    const courses: Ref<ICourse[]> = ref([]);
    const requestStatusValues: Ref<string[]> = ref(Object.keys(RequestStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveRequestedCourse = async requestedCourseId => {
      try {
        const res = await requestedCourseService().find(requestedCourseId);
        res.actualStartDate = new Date(res.actualStartDate);
        res.actualEndDate = new Date(res.actualEndDate);
        requestedCourse.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.requestedCourseId) {
      retrieveRequestedCourse(route.params.requestedCourseId);
    }

    const initRelationships = () => {
      trainingRequestService()
        .retrieve()
        .then(res => {
          trainingRequests.value = res.data;
        });
      courseService()
        .retrieve()
        .then(res => {
          courses.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      numberOfParticipants: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
      },
      preferredStartDate: {},
      preferredLocation: {
        required: validations.required('This field is required.'),
      },
      status: {},
      actualStartDate: {},
      actualEndDate: {},
      trainingRequest: {},
      course: {},
    };
    const v$ = useVuelidate(validationRules, requestedCourse as any);
    v$.value.$validate();

    return {
      requestedCourseService,
      alertService,
      requestedCourse,
      previousState,
      requestStatusValues,
      isSaving,
      currentLanguage,
      trainingRequests,
      courses,
      v$,
      ...useDateFormat({ entityRef: requestedCourse }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.requestedCourse.id) {
        this.requestedCourseService()
          .update(this.requestedCourse)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A RequestedCourse is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.requestedCourseService()
          .create(this.requestedCourse)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A RequestedCourse is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
