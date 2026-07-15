import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { type IInstructor, Instructor } from '@/shared/model/instructor.model';

import InstructorService from './instructor.service';

export default defineComponent({
  name: 'InstructorUpdate',
  setup() {
    const instructorService = inject('instructorService', () => new InstructorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instructor: Ref<IInstructor> = ref(new Instructor());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInstructor = async instructorId => {
      try {
        const res = await instructorService().find(instructorId);
        instructor.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instructorId) {
      retrieveInstructor(route.params.instructorId);
    }

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      contact: {},
      hasCar: {},
    };
    const v$ = useVuelidate(validationRules, instructor as any);
    v$.value.$validate();

    return {
      instructorService,
      alertService,
      instructor,
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
      if (this.instructor.id) {
        this.instructorService()
          .update(this.instructor)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Instructor is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.instructorService()
          .create(this.instructor)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Instructor is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
