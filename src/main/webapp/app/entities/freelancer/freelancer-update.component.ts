import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { Freelancer, type IFreelancer } from '@/shared/model/freelancer.model';

import FreelancerService from './freelancer.service';

export default defineComponent({
  name: 'FreelancerUpdate',
  setup() {
    const freelancerService = inject('freelancerService', () => new FreelancerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const freelancer: Ref<IFreelancer> = ref(new Freelancer());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveFreelancer = async freelancerId => {
      try {
        const res = await freelancerService().find(freelancerId);
        freelancer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.freelancerId) {
      retrieveFreelancer(route.params.freelancerId);
    }

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      contact: {},
      qualification: {},
    };
    const v$ = useVuelidate(validationRules, freelancer as any);
    v$.value.$validate();

    return {
      freelancerService,
      alertService,
      freelancer,
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
      if (this.freelancer.id) {
        this.freelancerService()
          .update(this.freelancer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Freelancer is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.freelancerService()
          .create(this.freelancer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Freelancer is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
