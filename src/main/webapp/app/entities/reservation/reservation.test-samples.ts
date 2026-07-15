import dayjs from 'dayjs/esm';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 30105,
  reservationDate: dayjs('2026-07-15T04:27'),
  status: 'PENDING',
};

export const sampleWithPartialData: IReservation = {
  id: 15924,
  reservationDate: dayjs('2026-07-14T23:38'),
  expirationDate: dayjs('2026-07-15T05:04'),
  status: 'CANCELLED',
};

export const sampleWithFullData: IReservation = {
  id: 27073,
  reservationDate: dayjs('2026-07-14T20:57'),
  expirationDate: dayjs('2026-07-15T03:14'),
  queuePosition: 4401,
  status: 'EXPIRED',
};

export const sampleWithNewData: NewReservation = {
  reservationDate: dayjs('2026-07-14T22:25'),
  status: 'COMPLETED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
