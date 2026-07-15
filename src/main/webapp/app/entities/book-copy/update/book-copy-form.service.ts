import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBookCopy, NewBookCopy } from '../book-copy.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookCopy for edit and NewBookCopyFormGroupInput for create.
 */
type BookCopyFormGroupInput = IBookCopy | PartialWithRequiredKeyOf<NewBookCopy>;

type BookCopyFormDefaults = Pick<NewBookCopy, 'id'>;

type BookCopyFormGroupContent = {
  id: FormControl<IBookCopy['id'] | NewBookCopy['id']>;
  barcode: FormControl<IBookCopy['barcode']>;
  shelfLocation: FormControl<IBookCopy['shelfLocation']>;
  acquisitionDate: FormControl<IBookCopy['acquisitionDate']>;
  status: FormControl<IBookCopy['status']>;
  book: FormControl<IBookCopy['book']>;
};

export type BookCopyFormGroup = FormGroup<BookCopyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookCopyFormService {
  createBookCopyFormGroup(bookCopy?: BookCopyFormGroupInput): BookCopyFormGroup {
    const bookCopyRawValue = {
      ...this.getFormDefaults(),
      ...(bookCopy ?? { id: null }),
    };
    return new FormGroup<BookCopyFormGroupContent>({
      id: new FormControl(
        { value: bookCopyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      barcode: new FormControl(bookCopyRawValue.barcode, {
        validators: [Validators.required],
      }),
      shelfLocation: new FormControl(bookCopyRawValue.shelfLocation, {
        validators: [Validators.required],
      }),
      acquisitionDate: new FormControl(bookCopyRawValue.acquisitionDate),
      status: new FormControl(bookCopyRawValue.status, {
        validators: [Validators.required],
      }),
      book: new FormControl(bookCopyRawValue.book),
    });
  }

  getBookCopy(form: BookCopyFormGroup): IBookCopy | NewBookCopy {
    return form.getRawValue();
  }

  resetForm(form: BookCopyFormGroup, bookCopy: BookCopyFormGroupInput): void {
    const bookCopyRawValue = { ...this.getFormDefaults(), ...bookCopy };
    form.reset({
      ...bookCopyRawValue,
      id: { value: bookCopyRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BookCopyFormDefaults {
    return {
      id: null,
    };
  }
}
