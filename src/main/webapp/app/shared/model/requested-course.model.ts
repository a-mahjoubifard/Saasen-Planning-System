import { type ICourse } from '@/shared/model/course.model';
import { type RequestStatus } from '@/shared/model/enumerations/request-status.model';
import { type ITrainingRequest } from '@/shared/model/training-request.model';
export interface IRequestedCourse {
  id?: number;
  numberOfParticipants?: number;
  preferredStartDate?: Date | null;
  preferredLocation?: string;
  status?: keyof typeof RequestStatus | null;
  actualStartDate?: Date | null;
  actualEndDate?: Date | null;
  trainingRequest?: ITrainingRequest | null;
  course?: ICourse | null;
}

export class RequestedCourse implements IRequestedCourse {
  constructor(
    public id?: number,
    public numberOfParticipants?: number,
    public preferredStartDate?: Date | null,
    public preferredLocation?: string,
    public status?: keyof typeof RequestStatus | null,
    public actualStartDate?: Date | null,
    public actualEndDate?: Date | null,
    public trainingRequest?: ITrainingRequest | null,
    public course?: ICourse | null,
  ) {}
}
