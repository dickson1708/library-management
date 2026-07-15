import dayjs from 'dayjs/esm';

import { IBookCopy, NewBookCopy } from './book-copy.model';

export const sampleWithRequiredData: IBookCopy = {
  id: 5875,
  barcode: 'spellcheck before up',
  shelfLocation: 'gently',
  status: 'LOST',
};

export const sampleWithPartialData: IBookCopy = {
  id: 28298,
  barcode: 'safely',
  shelfLocation: 'follower keel fort',
  acquisitionDate: dayjs('2026-07-14'),
  status: 'LOANED',
};

export const sampleWithFullData: IBookCopy = {
  id: 7148,
  barcode: 'smoothly frenetically',
  shelfLocation: 'boohoo next ordinary',
  acquisitionDate: dayjs('2026-07-14'),
  status: 'REPAIR',
};

export const sampleWithNewData: NewBookCopy = {
  barcode: 'ew unlike',
  shelfLocation: 'for publication',
  status: 'LOST',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
