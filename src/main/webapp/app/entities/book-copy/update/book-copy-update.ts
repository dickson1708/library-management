import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { BookCopyStatus } from 'app/entities/enumerations/book-copy-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';

import { BookCopyFormGroup, BookCopyFormService } from './book-copy-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-book-copy-update',
  templateUrl: './book-copy-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class BookCopyUpdate implements OnInit {
  readonly isSaving = signal(false);
  bookCopy: IBookCopy | null = null;
  bookCopyStatusValues = Object.keys(BookCopyStatus);

  booksSharedCollection = signal<IBook[]>([]);

  protected bookCopyService = inject(BookCopyService);
  protected bookCopyFormService = inject(BookCopyFormService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookCopyFormGroup = this.bookCopyFormService.createBookCopyFormGroup();

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookCopy }) => {
      this.bookCopy = bookCopy;
      if (bookCopy) {
        this.updateForm(bookCopy);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const bookCopy = this.bookCopyFormService.getBookCopy(this.editForm);
    if (bookCopy.id === null) {
      this.subscribeToSaveResponse(this.bookCopyService.create(bookCopy));
    } else {
      this.subscribeToSaveResponse(this.bookCopyService.update(bookCopy));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBookCopy | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(bookCopy: IBookCopy): void {
    this.bookCopy = bookCopy;
    this.bookCopyFormService.resetForm(this.editForm, bookCopy);

    this.booksSharedCollection.update(books => this.bookService.addBookToCollectionIfMissing<IBook>(books, bookCopy.book));
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.bookCopy?.book)))
      .subscribe((books: IBook[]) => this.booksSharedCollection.set(books));
  }
}
