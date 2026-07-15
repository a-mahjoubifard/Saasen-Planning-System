export interface ICustomer {
  id?: number;
  name?: string;
  contactInfo?: string | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public name?: string,
    public contactInfo?: string | null,
  ) {}
}
