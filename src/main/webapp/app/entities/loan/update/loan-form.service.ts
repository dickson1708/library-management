import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILoan, NewLoan } from '../loan.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILoan for edit and NewLoanFormGroupInput for create.
 */
type LoanFormGroupInput = ILoan | PartialWithRequiredKeyOf<NewLoan>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILoan | NewLoan> = Omit<T, 'loanDate' | 'dueDate' | 'returnDate'> & {
  loanDate?: string | null;
  dueDate?: string | null;
  returnDate?: string | null;
};

type LoanFormRawValue = FormValueOf<ILoan>;

type NewLoanFormRawValue = FormValueOf<NewLoan>;

type LoanFormDefaults = Pick<NewLoan, 'id' | 'loanDate' | 'dueDate' | 'returnDate'>;

type LoanFormGroupContent = {
  id: FormControl<LoanFormRawValue['id'] | NewLoan['id']>;
  loanDate: FormControl<LoanFormRawValue['loanDate']>;
  dueDate: FormControl<LoanFormRawValue['dueDate']>;
  returnDate: FormControl<LoanFormRawValue['returnDate']>;
  renewals: FormControl<LoanFormRawValue['renewals']>;
  notes: FormControl<LoanFormRawValue['notes']>;
  status: FormControl<LoanFormRawValue['status']>;
  member: FormControl<LoanFormRawValue['member']>;
  bookCopy: FormControl<LoanFormRawValue['bookCopy']>;
};

export type LoanFormGroup = FormGroup<LoanFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LoanFormService {
  createLoanFormGroup(loan?: LoanFormGroupInput): LoanFormGroup {
    const loanRawValue = this.convertLoanToLoanRawValue({
      ...this.getFormDefaults(),
      ...(loan ?? { id: null }),
    });
    return new FormGroup<LoanFormGroupContent>({
      id: new FormControl(
        { value: loanRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      loanDate: new FormControl(loanRawValue.loanDate, {
        validators: [Validators.required],
      }),
      dueDate: new FormControl(loanRawValue.dueDate, {
        validators: [Validators.required],
      }),
      returnDate: new FormControl(loanRawValue.returnDate),
      renewals: new FormControl(loanRawValue.renewals, {
        validators: [Validators.min(0)],
      }),
      notes: new FormControl(loanRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      status: new FormControl(loanRawValue.status, {
        validators: [Validators.required],
      }),
      member: new FormControl(loanRawValue.member),
      bookCopy: new FormControl(loanRawValue.bookCopy),
    });
  }

  getLoan(form: LoanFormGroup): ILoan | NewLoan {
    return this.convertLoanRawValueToLoan(form.getRawValue());
  }

  resetForm(form: LoanFormGroup, loan: LoanFormGroupInput): void {
    const loanRawValue = this.convertLoanToLoanRawValue({ ...this.getFormDefaults(), ...loan });
    form.reset({
      ...loanRawValue,
      id: { value: loanRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LoanFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      loanDate: currentTime,
      dueDate: currentTime,
      returnDate: currentTime,
    };
  }

  private convertLoanRawValueToLoan(rawLoan: LoanFormRawValue | NewLoanFormRawValue): ILoan | NewLoan {
    return {
      ...rawLoan,
      loanDate: dayjs(rawLoan.loanDate, DATE_TIME_FORMAT),
      dueDate: dayjs(rawLoan.dueDate, DATE_TIME_FORMAT),
      returnDate: dayjs(rawLoan.returnDate, DATE_TIME_FORMAT),
    };
  }

  private convertLoanToLoanRawValue(
    loan: ILoan | (Partial<NewLoan> & LoanFormDefaults),
  ): LoanFormRawValue | PartialWithRequiredKeyOf<NewLoanFormRawValue> {
    return {
      ...loan,
      loanDate: loan.loanDate ? loan.loanDate.format(DATE_TIME_FORMAT) : undefined,
      dueDate: loan.dueDate ? loan.dueDate.format(DATE_TIME_FORMAT) : undefined,
      returnDate: loan.returnDate ? loan.returnDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
