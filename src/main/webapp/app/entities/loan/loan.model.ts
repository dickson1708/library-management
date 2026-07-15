import dayjs from 'dayjs/esm';

import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { LoanStatus } from 'app/entities/enumerations/loan-status.model';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';

export interface ILoan {
  id: number;
  loanDate?: dayjs.Dayjs | null;
  dueDate?: dayjs.Dayjs | null;
  returnDate?: dayjs.Dayjs | null;
  renewals?: number | null;
  notes?: string | null;
  status?: keyof typeof LoanStatus | null;
  member?: Pick<ILibraryMember, 'id' | 'membershipNumber'> | null;
  bookCopy?: Pick<IBookCopy, 'id' | 'barcode'> | null;
}

export type NewLoan = Omit<ILoan, 'id'> & { id: null };
