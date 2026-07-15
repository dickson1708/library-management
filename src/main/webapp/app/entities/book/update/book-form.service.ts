import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBook, NewBook } from '../book.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBook for edit and NewBookFormGroupInput for create.
 */
type BookFormGroupInput = IBook | PartialWithRequiredKeyOf<NewBook>;

type BookFormDefaults = Pick<NewBook, 'id' | 'availableForLoan'>;

type BookFormGroupContent = {
  id: FormControl<IBook['id'] | NewBook['id']>;
  isbn: FormControl<IBook['isbn']>;
  title: FormControl<IBook['title']>;
  author: FormControl<IBook['author']>;
  publisher: FormControl<IBook['publisher']>;
  publicationDate: FormControl<IBook['publicationDate']>;
  language: FormControl<IBook['language']>;
  pages: FormControl<IBook['pages']>;
  description: FormControl<IBook['description']>;
  coverImage: FormControl<IBook['coverImage']>;
  availableForLoan: FormControl<IBook['availableForLoan']>;
  category: FormControl<IBook['category']>;
};

export type BookFormGroup = FormGroup<BookFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookFormService {
  createBookFormGroup(book?: BookFormGroupInput): BookFormGroup {
    const bookRawValue = {
      ...this.getFormDefaults(),
      ...(book ?? { id: null }),
    };
    return new FormGroup<BookFormGroupContent>({
      id: new FormControl(
        { value: bookRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      isbn: new FormControl(bookRawValue.isbn, {
        validators: [Validators.required],
      }),
      title: new FormControl(bookRawValue.title, {
        validators: [Validators.required],
      }),
      author: new FormControl(bookRawValue.author, {
        validators: [Validators.required],
      }),
      publisher: new FormControl(bookRawValue.publisher),
      publicationDate: new FormControl(bookRawValue.publicationDate),
      language: new FormControl(bookRawValue.language),
      pages: new FormControl(bookRawValue.pages, {
        validators: [Validators.min(1)],
      }),
      description: new FormControl(bookRawValue.description),
      coverImage: new FormControl(bookRawValue.coverImage),
      availableForLoan: new FormControl(bookRawValue.availableForLoan, {
        validators: [Validators.required],
      }),
      category: new FormControl(bookRawValue.category),
    });
  }

  getBook(form: BookFormGroup): IBook | NewBook {
    return form.getRawValue();
  }

  resetForm(form: BookFormGroup, book: BookFormGroupInput): void {
    const bookRawValue = { ...this.getFormDefaults(), ...book };
    form.reset({
      ...bookRawValue,
      id: { value: bookRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BookFormDefaults {
    return {
      id: null,
      availableForLoan: false,
    };
  }
}
