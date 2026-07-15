import { defineComponent, provide } from 'vue';

import UserService from '@/entities/user/user.service';

import AssignedResourceService from './assigned-resource/assigned-resource.service';
import CourseService from './course/course.service';
import CustomerService from './customer/customer.service';
import FreelancerService from './freelancer/freelancer.service';
import FreelancerAssignmentRequestService from './freelancer-assignment-request/freelancer-assignment-request.service';
import InstructorService from './instructor/instructor.service';
import InstructorAvailabilityService from './instructor-availability/instructor-availability.service';
import LocationService from './location/location.service';
import LocationAvailabilityService from './location-availability/location-availability.service';
import RequestedCourseService from './requested-course/requested-course.service';
import ResourceService from './resource/resource.service';
import ResourceAvailabilityService from './resource-availability/resource-availability.service';
import SessionService from './session/session.service';
import TrainingRequestService from './training-request/training-request.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('customerService', () => new CustomerService());
    provide('courseService', () => new CourseService());
    provide('instructorService', () => new InstructorService());
    provide('trainingRequestService', () => new TrainingRequestService());
    provide('requestedCourseService', () => new RequestedCourseService());
    provide('sessionService', () => new SessionService());
    provide('instructorAvailabilityService', () => new InstructorAvailabilityService());
    provide('locationAvailabilityService', () => new LocationAvailabilityService());
    provide('locationService', () => new LocationService());
    provide('resourceService', () => new ResourceService());
    provide('resourceAvailabilityService', () => new ResourceAvailabilityService());
    provide('assignedResourceService', () => new AssignedResourceService());
    provide('freelancerService', () => new FreelancerService());
    provide('freelancerAssignmentRequestService', () => new FreelancerAssignmentRequestService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
