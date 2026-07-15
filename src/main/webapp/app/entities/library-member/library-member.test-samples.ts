import dayjs from 'dayjs/esm';

import { ILibraryMember, NewLibraryMember } from './library-member.model';

export const sampleWithRequiredData: ILibraryMember = {
  id: 9734,
  membershipNumber: 11882,
  phoneNumber: 'who yearningly brr',
  registrationDate: dayjs('2026-07-15'),
  status: 'BLOCKED',
};

export const sampleWithPartialData: ILibraryMember = {
  id: 9060,
  membershipNumber: 28517,
  phoneNumber: 'jaunty',
  address: 'afore',
  registrationDate: dayjs('2026-07-15'),
  status: 'BLOCKED',
};

export const sampleWithFullData: ILibraryMember = {
  id: 4632,
  membershipNumber: 20064,
  phoneNumber: 'gosh so',
  birthDate: dayjs('2026-07-15'),
  address: 'blissfully',
  registrationDate: dayjs('2026-07-15'),
  status: 'BLOCKED',
};

export const sampleWithNewData: NewLibraryMember = {
  membershipNumber: 5282,
  phoneNumber: 'palate',
  registrationDate: dayjs('2026-07-14'),
  status: 'SUSPENDED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
