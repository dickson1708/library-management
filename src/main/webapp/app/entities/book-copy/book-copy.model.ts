import dayjs from 'dayjs/esm';

import { IBook } from 'app/entities/book/book.model';
import { BookCopyStatus } from 'app/entities/enumerations/book-copy-status.model';

export interface IBookCopy {
  id: number;
  barcode?: string | null;
  shelfLocation?: string | null;
  acquisitionDate?: dayjs.Dayjs | null;
  status?: keyof typeof BookCopyStatus | null;
  book?: Pick<IBook, 'id' | 'title'> | null;
}

export type NewBookCopy = Omit<IBookCopy, 'id'> & { id: null };
