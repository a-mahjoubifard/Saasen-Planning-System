import { type IResource } from '@/shared/model/resource.model';

export interface IResourceAvailability {
  id?: number;
  availableFrom?: Date | null;
  availableTo?: Date | null;
  resource?: IResource | null;
}

export class ResourceAvailability implements IResourceAvailability {
  constructor(
    public id?: number,
    public availableFrom?: Date | null,
    public availableTo?: Date | null,
    public resource?: IResource | null,
  ) {}
}
