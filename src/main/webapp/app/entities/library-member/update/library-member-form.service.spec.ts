import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../library-member.test-samples';

import { LibraryMemberFormService } from './library-member-form.service';

describe('LibraryMember Form Service', () => {
  let service: LibraryMemberFormService;

  beforeEach(() => {
    service = TestBed.inject(LibraryMemberFormService);
  });

  describe('Service methods', () => {
    describe('createLibraryMemberFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLibraryMemberFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            membershipNumber: expect.any(Object),
            phoneNumber: expect.any(Object),
            birthDate: expect.any(Object),
            address: expect.any(Object),
            registrationDate: expect.any(Object),
            status: expect.any(Object),
            internalUser: expect.any(Object),
          }),
        );
      });

      it('passing ILibraryMember should create a new form with FormGroup', () => {
        const formGroup = service.createLibraryMemberFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            membershipNumber: expect.any(Object),
            phoneNumber: expect.any(Object),
            birthDate: expect.any(Object),
            address: expect.any(Object),
            registrationDate: expect.any(Object),
            status: expect.any(Object),
            internalUser: expect.any(Object),
          }),
        );
      });
    });

    describe('getLibraryMember', () => {
      it('should return NewLibraryMember for default LibraryMember initial value', () => {
        const formGroup = service.createLibraryMemberFormGroup(sampleWithNewData);

        const libraryMember = service.getLibraryMember(formGroup);

        expect(libraryMember).toMatchObject(sampleWithNewData);
      });

      it('should return NewLibraryMember for empty LibraryMember initial value', () => {
        const formGroup = service.createLibraryMemberFormGroup();

        const libraryMember = service.getLibraryMember(formGroup);

        expect(libraryMember).toMatchObject({});
      });

      it('should return ILibraryMember', () => {
        const formGroup = service.createLibraryMemberFormGroup(sampleWithRequiredData);

        const libraryMember = service.getLibraryMember(formGroup);

        expect(libraryMember).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILibraryMember should not enable id FormControl', () => {
        const formGroup = service.createLibraryMemberFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLibraryMember should disable id FormControl', () => {
        const formGroup = service.createLibraryMemberFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
