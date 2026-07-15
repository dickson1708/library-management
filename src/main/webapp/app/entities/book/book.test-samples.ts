import dayjs from 'dayjs/esm';

import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 3991,
  isbn: 'blue regal',
  title: 'nocturnal wordy',
  author: 'for as',
  availableForLoan: false,
};

export const sampleWithPartialData: IBook = {
  id: 243,
  isbn: 'or the yuck',
  title: 'uh-huh gleaming',
  author: 'if defiantly',
  coverImage: 'stabilise',
  availableForLoan: false,
};

export const sampleWithFullData: IBook = {
  id: 8637,
  isbn: 'sniff',
  title: 'however quicker',
  author: 'reasoning repeatedly instead',
  publisher: 'blushing shipper gosh',
  publicationDate: dayjs('2026-07-15'),
  language: 'transom',
  pages: 16252,
  description: '../fake-data/blob/hipster.txt',
  coverImage: 'sick so putrefy',
  availableForLoan: true,
};

export const sampleWithNewData: NewBook = {
  isbn: 'taxicab nor',
  title: 'agreeable boo',
  author: 'boyfriend curiously although',
  availableForLoan: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
