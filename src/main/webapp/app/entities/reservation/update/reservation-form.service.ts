import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservation, NewReservation } from '../reservation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservation for edit and NewReservationFormGroupInput for create.
 */
type ReservationFormGroupInput = IReservation | PartialWithRequiredKeyOf<NewReservation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservation | NewReservation> = Omit<T, 'reservationDate' | 'expirationDate'> & {
  reservationDate?: string | null;
  expirationDate?: string | null;
};

type ReservationFormRawValue = FormValueOf<IReservation>;

type NewReservationFormRawValue = FormValueOf<NewReservation>;

type ReservationFormDefaults = Pick<NewReservation, 'id' | 'reservationDate' | 'expirationDate'>;

type ReservationFormGroupContent = {
  id: FormControl<ReservationFormRawValue['id'] | NewReservation['id']>;
  reservationDate: FormControl<ReservationFormRawValue['reservationDate']>;
  expirationDate: FormControl<ReservationFormRawValue['expirationDate']>;
  queuePosition: FormControl<ReservationFormRawValue['queuePosition']>;
  status: FormControl<ReservationFormRawValue['status']>;
  member: FormControl<ReservationFormRawValue['member']>;
  book: FormControl<ReservationFormRawValue['book']>;
};

export type ReservationFormGroup = FormGroup<ReservationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservationFormService {
  createReservationFormGroup(reservation?: ReservationFormGroupInput): ReservationFormGroup {
    const reservationRawValue = this.convertReservationToReservationRawValue({
      ...this.getFormDefaults(),
      ...(reservation ?? { id: null }),
    });
    return new FormGroup<ReservationFormGroupContent>({
      id: new FormControl(
        { value: reservationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reservationDate: new FormControl(reservationRawValue.reservationDate, {
        validators: [Validators.required],
      }),
      expirationDate: new FormControl(reservationRawValue.expirationDate),
      queuePosition: new FormControl(reservationRawValue.queuePosition),
      status: new FormControl(reservationRawValue.status, {
        validators: [Validators.required],
      }),
      member: new FormControl(reservationRawValue.member),
      book: new FormControl(reservationRawValue.book),
    });
  }

  getReservation(form: ReservationFormGroup): IReservation | NewReservation {
    return this.convertReservationRawValueToReservation(form.getRawValue());
  }

  resetForm(form: ReservationFormGroup, reservation: ReservationFormGroupInput): void {
    const reservationRawValue = this.convertReservationToReservationRawValue({ ...this.getFormDefaults(), ...reservation });
    form.reset({
      ...reservationRawValue,
      id: { value: reservationRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ReservationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      reservationDate: currentTime,
      expirationDate: currentTime,
    };
  }

  private convertReservationRawValueToReservation(
    rawReservation: ReservationFormRawValue | NewReservationFormRawValue,
  ): IReservation | NewReservation {
    return {
      ...rawReservation,
      reservationDate: dayjs(rawReservation.reservationDate, DATE_TIME_FORMAT),
      expirationDate: dayjs(rawReservation.expirationDate, DATE_TIME_FORMAT),
    };
  }

  private convertReservationToReservationRawValue(
    reservation: IReservation | (Partial<NewReservation> & ReservationFormDefaults),
  ): ReservationFormRawValue | PartialWithRequiredKeyOf<NewReservationFormRawValue> {
    return {
      ...reservation,
      reservationDate: reservation.reservationDate ? reservation.reservationDate.format(DATE_TIME_FORMAT) : undefined,
      expirationDate: reservation.expirationDate ? reservation.expirationDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
