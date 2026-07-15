import dayjs from 'dayjs/esm';

import { ICategory } from 'app/entities/category/category.model';

export interface IBook {
  id: number;
  isbn?: string | null;
  title?: string | null;
  author?: string | null;
  publisher?: string | null;
  publicationDate?: dayjs.Dayjs | null;
  language?: string | null;
  pages?: number | null;
  description?: string | null;
  coverImage?: string | null;
  availableForLoan?: boolean | null;
  category?: Pick<ICategory, 'id' | 'name'> | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
