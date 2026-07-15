import { type ILocation } from '@/shared/model/location.model';

export interface ILocationAvailability {
  id?: number;
  title?: string;
  availableFrom?: Date | null;
  availableTo?: Date | null;
  location?: ILocation | null;
}

export class LocationAvailability implements ILocationAvailability {
  constructor(
    public id?: number,
    public title?: string,
    public availableFrom?: Date | null,
    public availableTo?: Date | null,
    public location?: ILocation | null,
  ) {}
}
