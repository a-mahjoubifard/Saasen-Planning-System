import { Authority } from '@/shared/jhipster/constants';
const Entities = () => import('@/entities/entities.vue');

const Customer = () => import('@/entities/customer/customer.vue');
const CustomerUpdate = () => import('@/entities/customer/customer-update.vue');
const CustomerDetails = () => import('@/entities/customer/customer-details.vue');

const Course = () => import('@/entities/course/course.vue');
const CourseUpdate = () => import('@/entities/course/course-update.vue');
const CourseDetails = () => import('@/entities/course/course-details.vue');

const Instructor = () => import('@/entities/instructor/instructor.vue');
const InstructorUpdate = () => import('@/entities/instructor/instructor-update.vue');
const InstructorDetails = () => import('@/entities/instructor/instructor-details.vue');

const TrainingRequest = () => import('@/entities/training-request/training-request.vue');
const TrainingRequestUpdate = () => import('@/entities/training-request/training-request-update.vue');
const TrainingRequestDetails = () => import('@/entities/training-request/training-request-details.vue');

const RequestedCourse = () => import('@/entities/requested-course/requested-course.vue');
const RequestedCourseUpdate = () => import('@/entities/requested-course/requested-course-update.vue');
const RequestedCourseDetails = () => import('@/entities/requested-course/requested-course-details.vue');

const Session = () => import('@/entities/session/session.vue');
const SessionUpdate = () => import('@/entities/session/session-update.vue');
const SessionDetails = () => import('@/entities/session/session-details.vue');

const InstructorAvailability = () => import('@/entities/instructor-availability/instructor-availability.vue');
const InstructorAvailabilityUpdate = () => import('@/entities/instructor-availability/instructor-availability-update.vue');
const InstructorAvailabilityDetails = () => import('@/entities/instructor-availability/instructor-availability-details.vue');

const LocationAvailability = () => import('@/entities/location-availability/location-availability.vue');
const LocationAvailabilityUpdate = () => import('@/entities/location-availability/location-availability-update.vue');
const LocationAvailabilityDetails = () => import('@/entities/location-availability/location-availability-details.vue');

const Location = () => import('@/entities/location/location.vue');
const LocationUpdate = () => import('@/entities/location/location-update.vue');
const LocationDetails = () => import('@/entities/location/location-details.vue');

const Resource = () => import('@/entities/resource/resource.vue');
const ResourceUpdate = () => import('@/entities/resource/resource-update.vue');
const ResourceDetails = () => import('@/entities/resource/resource-details.vue');

const ResourceAvailability = () => import('@/entities/resource-availability/resource-availability.vue');
const ResourceAvailabilityUpdate = () => import('@/entities/resource-availability/resource-availability-update.vue');
const ResourceAvailabilityDetails = () => import('@/entities/resource-availability/resource-availability-details.vue');

const AssignedResource = () => import('@/entities/assigned-resource/assigned-resource.vue');
const AssignedResourceUpdate = () => import('@/entities/assigned-resource/assigned-resource-update.vue');
const AssignedResourceDetails = () => import('@/entities/assigned-resource/assigned-resource-details.vue');

const Freelancer = () => import('@/entities/freelancer/freelancer.vue');
const FreelancerUpdate = () => import('@/entities/freelancer/freelancer-update.vue');
const FreelancerDetails = () => import('@/entities/freelancer/freelancer-details.vue');

const FreelancerAssignmentRequest = () => import('@/entities/freelancer-assignment-request/freelancer-assignment-request.vue');
const FreelancerAssignmentRequestUpdate = () => import('@/entities/freelancer-assignment-request/freelancer-assignment-request-update.vue');
const FreelancerAssignmentRequestDetails = () =>
  import('@/entities/freelancer-assignment-request/freelancer-assignment-request-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'customer',
      name: 'Customer',
      component: Customer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/new',
      name: 'CustomerCreate',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/edit',
      name: 'CustomerEdit',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/view',
      name: 'CustomerView',
      component: CustomerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course',
      name: 'Course',
      component: Course,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/new',
      name: 'CourseCreate',
      component: CourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/:courseId/edit',
      name: 'CourseEdit',
      component: CourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/:courseId/view',
      name: 'CourseView',
      component: CourseDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor',
      name: 'Instructor',
      component: Instructor,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor/new',
      name: 'InstructorCreate',
      component: InstructorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor/:instructorId/edit',
      name: 'InstructorEdit',
      component: InstructorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor/:instructorId/view',
      name: 'InstructorView',
      component: InstructorDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'training-request',
      name: 'TrainingRequest',
      component: TrainingRequest,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'training-request/new',
      name: 'TrainingRequestCreate',
      component: TrainingRequestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'training-request/:trainingRequestId/edit',
      name: 'TrainingRequestEdit',
      component: TrainingRequestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'training-request/:trainingRequestId/view',
      name: 'TrainingRequestView',
      component: TrainingRequestDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'requested-course',
      name: 'RequestedCourse',
      component: RequestedCourse,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'requested-course/new',
      name: 'RequestedCourseCreate',
      component: RequestedCourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'requested-course/:requestedCourseId/edit',
      name: 'RequestedCourseEdit',
      component: RequestedCourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'requested-course/:requestedCourseId/view',
      name: 'RequestedCourseView',
      component: RequestedCourseDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'session',
      name: 'Session',
      component: Session,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'session/new',
      name: 'SessionCreate',
      component: SessionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'session/:sessionId/edit',
      name: 'SessionEdit',
      component: SessionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'session/:sessionId/view',
      name: 'SessionView',
      component: SessionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor-availability',
      name: 'InstructorAvailability',
      component: InstructorAvailability,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor-availability/new',
      name: 'InstructorAvailabilityCreate',
      component: InstructorAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor-availability/:instructorAvailabilityId/edit',
      name: 'InstructorAvailabilityEdit',
      component: InstructorAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instructor-availability/:instructorAvailabilityId/view',
      name: 'InstructorAvailabilityView',
      component: InstructorAvailabilityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location-availability',
      name: 'LocationAvailability',
      component: LocationAvailability,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location-availability/new',
      name: 'LocationAvailabilityCreate',
      component: LocationAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location-availability/:locationAvailabilityId/edit',
      name: 'LocationAvailabilityEdit',
      component: LocationAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location-availability/:locationAvailabilityId/view',
      name: 'LocationAvailabilityView',
      component: LocationAvailabilityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location',
      name: 'Location',
      component: Location,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/new',
      name: 'LocationCreate',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/edit',
      name: 'LocationEdit',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/view',
      name: 'LocationView',
      component: LocationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource',
      name: 'Resource',
      component: Resource,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource/new',
      name: 'ResourceCreate',
      component: ResourceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource/:resourceId/edit',
      name: 'ResourceEdit',
      component: ResourceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource/:resourceId/view',
      name: 'ResourceView',
      component: ResourceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource-availability',
      name: 'ResourceAvailability',
      component: ResourceAvailability,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource-availability/new',
      name: 'ResourceAvailabilityCreate',
      component: ResourceAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource-availability/:resourceAvailabilityId/edit',
      name: 'ResourceAvailabilityEdit',
      component: ResourceAvailabilityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'resource-availability/:resourceAvailabilityId/view',
      name: 'ResourceAvailabilityView',
      component: ResourceAvailabilityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'assigned-resource',
      name: 'AssignedResource',
      component: AssignedResource,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'assigned-resource/new',
      name: 'AssignedResourceCreate',
      component: AssignedResourceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'assigned-resource/:assignedResourceId/edit',
      name: 'AssignedResourceEdit',
      component: AssignedResourceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'assigned-resource/:assignedResourceId/view',
      name: 'AssignedResourceView',
      component: AssignedResourceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer',
      name: 'Freelancer',
      component: Freelancer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer/new',
      name: 'FreelancerCreate',
      component: FreelancerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer/:freelancerId/edit',
      name: 'FreelancerEdit',
      component: FreelancerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer/:freelancerId/view',
      name: 'FreelancerView',
      component: FreelancerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer-assignment-request',
      name: 'FreelancerAssignmentRequest',
      component: FreelancerAssignmentRequest,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer-assignment-request/new',
      name: 'FreelancerAssignmentRequestCreate',
      component: FreelancerAssignmentRequestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer-assignment-request/:freelancerAssignmentRequestId/edit',
      name: 'FreelancerAssignmentRequestEdit',
      component: FreelancerAssignmentRequestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'freelancer-assignment-request/:freelancerAssignmentRequestId/view',
      name: 'FreelancerAssignmentRequestView',
      component: FreelancerAssignmentRequestDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
