export interface IFreelancer {
  id?: number;
  name?: string;
  contact?: string | null;
  qualification?: string | null;
}

export class Freelancer implements IFreelancer {
  constructor(
    public id?: number,
    public name?: string,
    public contact?: string | null,
    public qualification?: string | null,
  ) {}
}
