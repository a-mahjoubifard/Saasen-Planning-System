import { type IInstructor } from '@/shared/model/instructor.model';

export interface IInstructorAvailability {
  id?: number;
  title?: string;
  availableFrom?: Date | null;
  availableTo?: Date | null;
  instructor?: IInstructor | null;
}

export class InstructorAvailability implements IInstructorAvailability {
  constructor(
    public id?: number,
    public title?: string,
    public availableFrom?: Date | null,
    public availableTo?: Date | null,
    public instructor?: IInstructor | null,
  ) {}
}
