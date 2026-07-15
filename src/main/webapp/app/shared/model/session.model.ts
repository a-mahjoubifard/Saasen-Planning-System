import { type RequestStatus } from '@/shared/model/enumerations/request-status.model';
import { type IFreelancer } from '@/shared/model/freelancer.model';
import { type IInstructorAvailability } from '@/shared/model/instructor-availability.model';
import { type ILocationAvailability } from '@/shared/model/location-availability.model';
import { type IRequestedCourse } from '@/shared/model/requested-course.model';

export interface ISession {
  id?: number;
  startDate?: Date | null;
  endDate?: Date | null;
  status?: keyof typeof RequestStatus | null;
  instructorAvailability?: IInstructorAvailability | null;
  locationAvailability?: ILocationAvailability | null;
  requestedCourse?: IRequestedCourse | null;
  freelancer?: IFreelancer | null;
}

export class Session implements ISession {
  constructor(
    public id?: number,
    public startDate?: Date | null,
    public endDate?: Date | null,
    public status?: keyof typeof RequestStatus | null,
    public instructorAvailability?: IInstructorAvailability | null,
    public locationAvailability?: ILocationAvailability | null,
    public requestedCourse?: IRequestedCourse | null,
    public freelancer?: IFreelancer | null,
  ) {}
}
