import { type ICustomer } from '@/shared/model/customer.model';

export interface ITrainingRequest {
  id?: number;
  requestDate?: Date | null;
  description?: string;
  customer?: ICustomer | null;
}

export class TrainingRequest implements ITrainingRequest {
  constructor(
    public id?: number,
    public requestDate?: Date | null,
    public description?: string,
    public customer?: ICustomer | null,
  ) {}
}
