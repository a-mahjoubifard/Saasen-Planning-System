import { type IResourceAvailability } from '@/shared/model/resource-availability.model';
import { type ISession } from '@/shared/model/session.model';

export interface IAssignedResource {
  id?: number;
  resourceAvailability?: IResourceAvailability | null;
  session?: ISession | null;
}

export class AssignedResource implements IAssignedResource {
  constructor(
    public id?: number,
    public resourceAvailability?: IResourceAvailability | null,
    public session?: ISession | null,
  ) {}
}
