import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILibraryMember, NewLibraryMember } from '../library-member.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILibraryMember for edit and NewLibraryMemberFormGroupInput for create.
 */
type LibraryMemberFormGroupInput = ILibraryMember | PartialWithRequiredKeyOf<NewLibraryMember>;

type LibraryMemberFormDefaults = Pick<NewLibraryMember, 'id'>;

type LibraryMemberFormGroupContent = {
  id: FormControl<ILibraryMember['id'] | NewLibraryMember['id']>;
  membershipNumber: FormControl<ILibraryMember['membershipNumber']>;
  phoneNumber: FormControl<ILibraryMember['phoneNumber']>;
  birthDate: FormControl<ILibraryMember['birthDate']>;
  address: FormControl<ILibraryMember['address']>;
  registrationDate: FormControl<ILibraryMember['registrationDate']>;
  status: FormControl<ILibraryMember['status']>;
  internalUser: FormControl<ILibraryMember['internalUser']>;
};

export type LibraryMemberFormGroup = FormGroup<LibraryMemberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LibraryMemberFormService {
  createLibraryMemberFormGroup(libraryMember?: LibraryMemberFormGroupInput): LibraryMemberFormGroup {
    const libraryMemberRawValue = {
      ...this.getFormDefaults(),
      ...(libraryMember ?? { id: null }),
    };
    return new FormGroup<LibraryMemberFormGroupContent>({
      id: new FormControl(
        { value: libraryMemberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      membershipNumber: new FormControl(libraryMemberRawValue.membershipNumber, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(libraryMemberRawValue.phoneNumber, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      birthDate: new FormControl(libraryMemberRawValue.birthDate),
      address: new FormControl(libraryMemberRawValue.address, {
        validators: [Validators.maxLength(255)],
      }),
      registrationDate: new FormControl(libraryMemberRawValue.registrationDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(libraryMemberRawValue.status, {
        validators: [Validators.required],
      }),
      internalUser: new FormControl(libraryMemberRawValue.internalUser),
    });
  }

  getLibraryMember(form: LibraryMemberFormGroup): ILibraryMember | NewLibraryMember {
    return form.getRawValue();
  }

  resetForm(form: LibraryMemberFormGroup, libraryMember: LibraryMemberFormGroupInput): void {
    const libraryMemberRawValue = { ...this.getFormDefaults(), ...libraryMember };
    form.reset({
      ...libraryMemberRawValue,
      id: { value: libraryMemberRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LibraryMemberFormDefaults {
    return {
      id: null,
    };
  }
}
