import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import InstructorService from '@/entities/instructor/instructor.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type IInstructorAvailability, InstructorAvailability } from '@/shared/model/instructor-availability.model';
import { type IInstructor } from '@/shared/model/instructor.model';

import InstructorAvailabilityService from './instructor-availability.service';

export default defineComponent({
  name: 'InstructorAvailabilityUpdate',
  setup() {
    const instructorAvailabilityService = inject('instructorAvailabilityService', () => new InstructorAvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instructorAvailability: Ref<IInstructorAvailability> = ref(new InstructorAvailability());

    const instructorService = inject('instructorService', () => new InstructorService());

    const instructors: Ref<IInstructor[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInstructorAvailability = async instructorAvailabilityId => {
      try {
        const res = await instructorAvailabilityService().find(instructorAvailabilityId);
        res.availableFrom = new Date(res.availableFrom);
        res.availableTo = new Date(res.availableTo);
        instructorAvailability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instructorAvailabilityId) {
      retrieveInstructorAvailability(route.params.instructorAvailabilityId);
    }

    const initRelationships = () => {
      instructorService()
        .retrieve()
        .then(res => {
          instructors.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('This field is required.'),
      },
      availableFrom: {},
      availableTo: {},
      instructor: {},
      session: {},
    };
    const v$ = useVuelidate(validationRules, instructorAvailability as any);
    v$.value.$validate();

    return {
      instructorAvailabilityService,
      alertService,
      instructorAvailability,
      previousState,
      isSaving,
      currentLanguage,
      instructors,
      v$,
      ...useDateFormat({ entityRef: instructorAvailability }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.instructorAvailability.id) {
        this.instructorAvailabilityService()
          .update(this.instructorAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A InstructorAvailability is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.instructorAvailabilityService()
          .create(this.instructorAvailability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A InstructorAvailability is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
