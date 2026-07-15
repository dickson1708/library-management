import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';
import { LibraryMemberService } from 'app/entities/library-member/service/library-member.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IReview } from '../review.model';
import { ReviewService } from '../service/review.service';

import { ReviewFormGroup, ReviewFormService } from './review-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-review-update',
  templateUrl: './review-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ReviewUpdate implements OnInit {
  readonly isSaving = signal(false);
  review: IReview | null = null;

  libraryMembersSharedCollection = signal<ILibraryMember[]>([]);
  booksSharedCollection = signal<IBook[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected reviewService = inject(ReviewService);
  protected reviewFormService = inject(ReviewFormService);
  protected libraryMemberService = inject(LibraryMemberService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReviewFormGroup = this.reviewFormService.createReviewFormGroup();

  compareLibraryMember = (o1: ILibraryMember | null, o2: ILibraryMember | null): boolean =>
    this.libraryMemberService.compareLibraryMember(o1, o2);

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ review }) => {
      this.review = review;
      if (review) {
        this.updateForm(review);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertErrorModel>('libraryManagementApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const review = this.reviewFormService.getReview(this.editForm);
    if (review.id === null) {
      this.subscribeToSaveResponse(this.reviewService.create(review));
    } else {
      this.subscribeToSaveResponse(this.reviewService.update(review));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IReview | null>): void {
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

  protected updateForm(review: IReview): void {
    this.review = review;
    this.reviewFormService.resetForm(this.editForm, review);

    this.libraryMembersSharedCollection.update(libraryMembers =>
      this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, review.member),
    );
    this.booksSharedCollection.update(books => this.bookService.addBookToCollectionIfMissing<IBook>(books, review.book));
  }

  protected loadRelationshipsOptions(): void {
    this.libraryMemberService
      .query()
      .pipe(map((res: HttpResponse<ILibraryMember[]>) => res.body ?? []))
      .pipe(
        map((libraryMembers: ILibraryMember[]) =>
          this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, this.review?.member),
        ),
      )
      .subscribe((libraryMembers: ILibraryMember[]) => this.libraryMembersSharedCollection.set(libraryMembers));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.review?.book)))
      .subscribe((books: IBook[]) => this.booksSharedCollection.set(books));
  }
}
