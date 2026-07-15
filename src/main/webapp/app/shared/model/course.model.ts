export interface ICourse {
  id?: number;
  title?: string;
  duration?: number | null;
}

export class Course implements ICourse {
  constructor(
    public id?: number,
    public title?: string,
    public duration?: number | null,
  ) {}
}
