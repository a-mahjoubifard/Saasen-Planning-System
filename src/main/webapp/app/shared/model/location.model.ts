export interface ILocation {
  id?: number;
  name?: string;
  address?: string | null;
  capacity?: number | null;
  equipmentType?: string | null;
}

export class Location implements ILocation {
  constructor(
    public id?: number,
    public name?: string,
    public address?: string | null,
    public capacity?: number | null,
    public equipmentType?: string | null,
  ) {}
}
