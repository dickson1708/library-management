import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBookCopy } from 'app/entities/book-copy/book-copy.model';
import { BookCopyService } from 'app/entities/book-copy/service/book-copy.service';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';
import { LibraryMemberService } from 'app/entities/library-member/service/library-member.service';
import { ILoan } from '../loan.model';
import { LoanService } from '../service/loan.service';

import { LoanFormService } from './loan-form.service';
import { LoanUpdate } from './loan-update';

describe('Loan Management Update Component', () => {
  let comp: LoanUpdate;
  let fixture: ComponentFixture<LoanUpdate>;
  let activatedRoute: ActivatedRoute;
  let loanFormService: LoanFormService;
  let loanService: LoanService;
  let libraryMemberService: LibraryMemberService;
  let bookCopyService: BookCopyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(LoanUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    loanFormService = TestBed.inject(LoanFormService);
    loanService = TestBed.inject(LoanService);
    libraryMemberService = TestBed.inject(LibraryMemberService);
    bookCopyService = TestBed.inject(BookCopyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LibraryMember query and add missing value', () => {
      const loan: ILoan = { id: 441 };
      const member: ILibraryMember = { id: 8289 };
      loan.member = member;

      const libraryMemberCollection: ILibraryMember[] = [{ id: 8289 }];
      vitest.spyOn(libraryMemberService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryMemberCollection })));
      const additionalLibraryMembers = [member];
      const expectedCollection: ILibraryMember[] = [...additionalLibraryMembers, ...libraryMemberCollection];
      vitest.spyOn(libraryMemberService, 'addLibraryMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ loan });
      comp.ngOnInit();

      expect(libraryMemberService.query).toHaveBeenCalled();
      expect(libraryMemberService.addLibraryMemberToCollectionIfMissing).toHaveBeenCalledWith(
        libraryMemberCollection,
        ...additionalLibraryMembers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.libraryMembersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call BookCopy query and add missing value', () => {
      const loan: ILoan = { id: 441 };
      const bookCopy: IBookCopy = { id: 11646 };
      loan.bookCopy = bookCopy;

      const bookCopyCollection: IBookCopy[] = [{ id: 11646 }];
      vitest.spyOn(bookCopyService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCopyCollection })));
      const additionalBookCopies = [bookCopy];
      const expectedCollection: IBookCopy[] = [...additionalBookCopies, ...bookCopyCollection];
      vitest.spyOn(bookCopyService, 'addBookCopyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ loan });
      comp.ngOnInit();

      expect(bookCopyService.query).toHaveBeenCalled();
      expect(bookCopyService.addBookCopyToCollectionIfMissing).toHaveBeenCalledWith(
        bookCopyCollection,
        ...additionalBookCopies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.bookCopiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const loan: ILoan = { id: 441 };
      const member: ILibraryMember = { id: 8289 };
      loan.member = member;
      const bookCopy: IBookCopy = { id: 11646 };
      loan.bookCopy = bookCopy;

      activatedRoute.data = of({ loan });
      comp.ngOnInit();

      expect(comp.libraryMembersSharedCollection()).toContainEqual(member);
      expect(comp.bookCopiesSharedCollection()).toContainEqual(bookCopy);
      expect(comp.loan).toEqual(loan);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILoan>();
      const loan = { id: 1685 };
      vitest.spyOn(loanFormService, 'getLoan').mockReturnValue(loan);
      vitest.spyOn(loanService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ loan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(loan);
      saveSubject.complete();

      // THEN
      expect(loanFormService.getLoan).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(loanService.update).toHaveBeenCalledWith(expect.objectContaining(loan));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILoan>();
      const loan = { id: 1685 };
      vitest.spyOn(loanFormService, 'getLoan').mockReturnValue({ id: null });
      vitest.spyOn(loanService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ loan: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(loan);
      saveSubject.complete();

      // THEN
      expect(loanFormService.getLoan).toHaveBeenCalled();
      expect(loanService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILoan>();
      const loan = { id: 1685 };
      vitest.spyOn(loanService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ loan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(loanService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLibraryMember', () => {
      it('should forward to libraryMemberService', () => {
        const entity = { id: 8289 };
        const entity2 = { id: 13667 };
        vitest.spyOn(libraryMemberService, 'compareLibraryMember');
        comp.compareLibraryMember(entity, entity2);
        expect(libraryMemberService.compareLibraryMember).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBookCopy', () => {
      it('should forward to bookCopyService', () => {
        const entity = { id: 11646 };
        const entity2 = { id: 4928 };
        vitest.spyOn(bookCopyService, 'compareBookCopy');
        comp.compareBookCopy(entity, entity2);
        expect(bookCopyService.compareBookCopy).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
