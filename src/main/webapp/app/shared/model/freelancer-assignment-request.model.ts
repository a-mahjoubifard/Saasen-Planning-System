import { type FreelancerRequestStatus } from '@/shared/model/enumerations/freelancer-request-status.model';
import { type IFreelancer } from '@/shared/model/freelancer.model';
import { type ISession } from '@/shared/model/session.model';

export interface IFreelancerAssignmentRequest {
  id?: number;
  requestedAt?: Date | null;
  status?: keyof typeof FreelancerRequestStatus;
  freelancer?: IFreelancer | null;
  session?: ISession | null;
}

export class FreelancerAssignmentRequest implements IFreelancerAssignmentRequest {
  constructor(
    public id?: number,
    public requestedAt?: Date | null,
    public status?: keyof typeof FreelancerRequestStatus,
    public freelancer?: IFreelancer | null,
    public session?: ISession | null,
  ) {}
}
