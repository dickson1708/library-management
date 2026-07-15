import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { LibraryMemberStatus } from 'app/entities/enumerations/library-member-status.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILibraryMember } from '../library-member.model';
import { LibraryMemberService } from '../service/library-member.service';

import { LibraryMemberFormGroup, LibraryMemberFormService } from './library-member-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-library-member-update',
  templateUrl: './library-member-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class LibraryMemberUpdate implements OnInit {
  readonly isSaving = signal(false);
  libraryMember: ILibraryMember | null = null;
  libraryMemberStatusValues = Object.keys(LibraryMemberStatus);

  usersSharedCollection = signal<IUser[]>([]);

  protected libraryMemberService = inject(LibraryMemberService);
  protected libraryMemberFormService = inject(LibraryMemberFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LibraryMemberFormGroup = this.libraryMemberFormService.createLibraryMemberFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ libraryMember }) => {
      this.libraryMember = libraryMember;
      if (libraryMember) {
        this.updateForm(libraryMember);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const libraryMember = this.libraryMemberFormService.getLibraryMember(this.editForm);
    if (libraryMember.id === null) {
      this.subscribeToSaveResponse(this.libraryMemberService.create(libraryMember));
    } else {
      this.subscribeToSaveResponse(this.libraryMemberService.update(libraryMember));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILibraryMember | null>): void {
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

  protected updateForm(libraryMember: ILibraryMember): void {
    this.libraryMember = libraryMember;
    this.libraryMemberFormService.resetForm(this.editForm, libraryMember);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, libraryMember.internalUser));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.libraryMember?.internalUser)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
