import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IFreelancer } from '@/shared/model/freelancer.model';

import FreelancerService from './freelancer.service';

export default defineComponent({
  name: 'FreelancerDetails',
  setup() {
    const freelancerService = inject('freelancerService', () => new FreelancerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const freelancer: Ref<IFreelancer> = ref({});

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

    return {
      alertService,
      freelancer,

      previousState,
    };
  },
});
