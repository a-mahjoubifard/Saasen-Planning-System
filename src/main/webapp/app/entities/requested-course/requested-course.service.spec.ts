import { beforeEach, describe, expect, it } from 'vitest';

import axios from 'axios';
import dayjs from 'dayjs';
import sinon from 'sinon';

import { DATE_FORMAT, DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { RequestedCourse } from '@/shared/model/requested-course.model';

import RequestedCourseService from './requested-course.service';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('RequestedCourse Service', () => {
    let service: RequestedCourseService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new RequestedCourseService();
      currentDate = new Date();
      elemDefault = new RequestedCourse(123, 0, currentDate, 'AAAAAAA', 'PENDING', currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          preferredStartDate: dayjs(currentDate).format(DATE_FORMAT),
          actualStartDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          actualEndDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a RequestedCourse', async () => {
        const returnedFromService = {
          id: 123,
          preferredStartDate: dayjs(currentDate).format(DATE_FORMAT),
          actualStartDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          actualEndDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = {
          preferredStartDate: currentDate,
          actualStartDate: currentDate,
          actualEndDate: currentDate,
          ...returnedFromService,
        };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a RequestedCourse', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a RequestedCourse', async () => {
        const returnedFromService = {
          numberOfParticipants: 1,
          preferredStartDate: dayjs(currentDate).format(DATE_FORMAT),
          preferredLocation: 'BBBBBB',
          status: 'BBBBBB',
          actualStartDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          actualEndDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = {
          preferredStartDate: currentDate,
          actualStartDate: currentDate,
          actualEndDate: currentDate,
          ...returnedFromService,
        };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a RequestedCourse', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a RequestedCourse', async () => {
        const patchObject = {
          numberOfParticipants: 1,
          actualEndDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...new RequestedCourse(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = {
          preferredStartDate: currentDate,
          actualStartDate: currentDate,
          actualEndDate: currentDate,
          ...returnedFromService,
        };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a RequestedCourse', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of RequestedCourse', async () => {
        const returnedFromService = {
          numberOfParticipants: 1,
          preferredStartDate: dayjs(currentDate).format(DATE_FORMAT),
          preferredLocation: 'BBBBBB',
          status: 'BBBBBB',
          actualStartDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          actualEndDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = {
          preferredStartDate: currentDate,
          actualStartDate: currentDate,
          actualEndDate: currentDate,
          ...returnedFromService,
        };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of RequestedCourse', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a RequestedCourse', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a RequestedCourse', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
