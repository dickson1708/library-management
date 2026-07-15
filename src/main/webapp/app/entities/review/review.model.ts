import dayjs from 'dayjs/esm';

import { IBook } from 'app/entities/book/book.model';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';

export interface IReview {
  id: number;
  rating?: number | null;
  comment?: string | null;
  createdDate?: dayjs.Dayjs | null;
  member?: Pick<ILibraryMember, 'id' | 'membershipNumber'> | null;
  book?: Pick<IBook, 'id' | 'title'> | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
