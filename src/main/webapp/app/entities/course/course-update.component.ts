import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { Course, type ICourse } from '@/shared/model/course.model';

import CourseService from './course.service';

export default defineComponent({
  name: 'CourseUpdate',
  setup() {
    const courseService = inject('courseService', () => new CourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const course: Ref<ICourse> = ref(new Course());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCourse = async courseId => {
      try {
        const res = await courseService().find(courseId);
        course.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseId) {
      retrieveCourse(route.params.courseId);
    }

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('This field is required.'),
      },
      duration: {},
    };
    const v$ = useVuelidate(validationRules, course as any);
    v$.value.$validate();

    return {
      courseService,
      alertService,
      course,
      previousState,
      isSaving,
      currentLanguage,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.course.id) {
        this.courseService()
          .update(this.course)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Course is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.courseService()
          .create(this.course)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Course is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
