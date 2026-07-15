export interface IInstructor {
  id?: number;
  name?: string;
  contact?: string | null;
  hasCar?: boolean | null;
}

export class Instructor implements IInstructor {
  constructor(
    public id?: number,
    public name?: string,
    public contact?: string | null,
    public hasCar?: boolean | null,
  ) {
    this.hasCar = this.hasCar ?? false;
  }
}
