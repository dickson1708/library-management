import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';
import { LibraryMemberService } from 'app/entities/library-member/service/library-member.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IReservation } from '../reservation.model';
import { ReservationService } from '../service/reservation.service';

import { ReservationFormGroup, ReservationFormService } from './reservation-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-reservation-update',
  templateUrl: './reservation-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ReservationUpdate implements OnInit {
  readonly isSaving = signal(false);
  reservation: IReservation | null = null;
  reservationStatusValues = Object.keys(ReservationStatus);

  libraryMembersSharedCollection = signal<ILibraryMember[]>([]);
  booksSharedCollection = signal<IBook[]>([]);

  protected reservationService = inject(ReservationService);
  protected reservationFormService = inject(ReservationFormService);
  protected libraryMemberService = inject(LibraryMemberService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReservationFormGroup = this.reservationFormService.createReservationFormGroup();

  compareLibraryMember = (o1: ILibraryMember | null, o2: ILibraryMember | null): boolean =>
    this.libraryMemberService.compareLibraryMember(o1, o2);

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservation }) => {
      this.reservation = reservation;
      if (reservation) {
        this.updateForm(reservation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const reservation = this.reservationFormService.getReservation(this.editForm);
    if (reservation.id === null) {
      this.subscribeToSaveResponse(this.reservationService.create(reservation));
    } else {
      this.subscribeToSaveResponse(this.reservationService.update(reservation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IReservation | null>): void {
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

  protected updateForm(reservation: IReservation): void {
    this.reservation = reservation;
    this.reservationFormService.resetForm(this.editForm, reservation);

    this.libraryMembersSharedCollection.update(libraryMembers =>
      this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, reservation.member),
    );
    this.booksSharedCollection.update(books => this.bookService.addBookToCollectionIfMissing<IBook>(books, reservation.book));
  }

  protected loadRelationshipsOptions(): void {
    this.libraryMemberService
      .query()
      .pipe(map((res: HttpResponse<ILibraryMember[]>) => res.body ?? []))
      .pipe(
        map((libraryMembers: ILibraryMember[]) =>
          this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, this.reservation?.member),
        ),
      )
      .subscribe((libraryMembers: ILibraryMember[]) => this.libraryMembersSharedCollection.set(libraryMembers));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.reservation?.book)))
      .subscribe((books: IBook[]) => this.booksSharedCollection.set(books));
  }
}
