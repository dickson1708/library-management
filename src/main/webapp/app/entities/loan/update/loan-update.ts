import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { BookCopyService } from 'app/entities/book-copy/service/book-copy.service';
import { LoanStatus } from 'app/entities/enumerations/loan-status.model';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';
import { LibraryMemberService } from 'app/entities/library-member/service/library-member.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILoan } from '../loan.model';
import { LoanService } from '../service/loan.service';

import { LoanFormGroup, LoanFormService } from './loan-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-loan-update',
  templateUrl: './loan-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LoanUpdate implements OnInit {
  readonly isSaving = signal(false);
  loan: ILoan | null = null;
  loanStatusValues = Object.keys(LoanStatus);

  libraryMembersSharedCollection = signal<ILibraryMember[]>([]);
  bookCopiesSharedCollection = signal<IBookCopy[]>([]);

  protected loanService = inject(LoanService);
  protected loanFormService = inject(LoanFormService);
  protected libraryMemberService = inject(LibraryMemberService);
  protected bookCopyService = inject(BookCopyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LoanFormGroup = this.loanFormService.createLoanFormGroup();

  compareLibraryMember = (o1: ILibraryMember | null, o2: ILibraryMember | null): boolean =>
    this.libraryMemberService.compareLibraryMember(o1, o2);

  compareBookCopy = (o1: IBookCopy | null, o2: IBookCopy | null): boolean => this.bookCopyService.compareBookCopy(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ loan }) => {
      this.loan = loan;
      if (loan) {
        this.updateForm(loan);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const loan = this.loanFormService.getLoan(this.editForm);
    if (loan.id === null) {
      this.subscribeToSaveResponse(this.loanService.create(loan));
    } else {
      this.subscribeToSaveResponse(this.loanService.update(loan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILoan | null>): void {
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

  protected updateForm(loan: ILoan): void {
    this.loan = loan;
    this.loanFormService.resetForm(this.editForm, loan);

    this.libraryMembersSharedCollection.update(libraryMembers =>
      this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, loan.member),
    );
    this.bookCopiesSharedCollection.update(bookCopies =>
      this.bookCopyService.addBookCopyToCollectionIfMissing<IBookCopy>(bookCopies, loan.bookCopy),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.libraryMemberService
      .query()
      .pipe(map((res: HttpResponse<ILibraryMember[]>) => res.body ?? []))
      .pipe(
        map((libraryMembers: ILibraryMember[]) =>
          this.libraryMemberService.addLibraryMemberToCollectionIfMissing<ILibraryMember>(libraryMembers, this.loan?.member),
        ),
      )
      .subscribe((libraryMembers: ILibraryMember[]) => this.libraryMembersSharedCollection.set(libraryMembers));

    this.bookCopyService
      .query()
      .pipe(map((res: HttpResponse<IBookCopy[]>) => res.body ?? []))
      .pipe(
        map((bookCopies: IBookCopy[]) => this.bookCopyService.addBookCopyToCollectionIfMissing<IBookCopy>(bookCopies, this.loan?.bookCopy)),
      )
      .subscribe((bookCopies: IBookCopy[]) => this.bookCopiesSharedCollection.set(bookCopies));
  }
}
