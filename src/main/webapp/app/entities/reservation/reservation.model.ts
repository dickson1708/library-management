import dayjs from 'dayjs/esm';

import { IBook } from 'app/entities/book/book.model';
import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';

export interface IReservation {
  id: number;
  reservationDate?: dayjs.Dayjs | null;
  expirationDate?: dayjs.Dayjs | null;
  queuePosition?: number | null;
  status?: keyof typeof ReservationStatus | null;
  member?: Pick<ILibraryMember, 'id' | 'membershipNumber'> | null;
  book?: Pick<IBook, 'id' | 'title'> | null;
}

export type NewReservation = Omit<IReservation, 'id'> & { id: null };
