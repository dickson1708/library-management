import dayjs from 'dayjs/esm';

import { LibraryMemberStatus } from 'app/entities/enumerations/library-member-status.model';
import { IUser } from 'app/entities/user/user.model';

export interface ILibraryMember {
  id: number;
  membershipNumber?: number | null;
  phoneNumber?: string | null;
  birthDate?: dayjs.Dayjs | null;
  address?: string | null;
  registrationDate?: dayjs.Dayjs | null;
  status?: keyof typeof LibraryMemberStatus | null;
  internalUser?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewLibraryMember = Omit<ILibraryMember, 'id'> & { id: null };
