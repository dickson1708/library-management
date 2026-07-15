import dayjs from 'dayjs/esm';

import { ILoan, NewLoan } from './loan.model';

export const sampleWithRequiredData: ILoan = {
  id: 12643,
  loanDate: dayjs('2026-07-15T06:04'),
  dueDate: dayjs('2026-07-15T05:06'),
  status: 'OVERDUE',
};

export const sampleWithPartialData: ILoan = {
  id: 8125,
  loanDate: dayjs('2026-07-15T11:51'),
  dueDate: dayjs('2026-07-15T11:58'),
  returnDate: dayjs('2026-07-15T08:55'),
  status: 'LOST',
};

export const sampleWithFullData: ILoan = {
  id: 27230,
  loanDate: dayjs('2026-07-15T06:47'),
  dueDate: dayjs('2026-07-15T10:57'),
  returnDate: dayjs('2026-07-14T17:12'),
  renewals: 2538,
  notes: 'modulo dampen',
  status: 'OVERDUE',
};

export const sampleWithNewData: NewLoan = {
  loanDate: dayjs('2026-07-15T01:17'),
  dueDate: dayjs('2026-07-15T06:31'),
  status: 'LOST',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
