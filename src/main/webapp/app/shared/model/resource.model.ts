export interface IResource {
  id?: number;
  name?: string;
  resourceType?: string | null;
  status?: string | null;
}

export class Resource implements IResource {
  constructor(
    public id?: number,
    public name?: string,
    public resourceType?: string | null,
    public status?: string | null,
  ) {}
}
