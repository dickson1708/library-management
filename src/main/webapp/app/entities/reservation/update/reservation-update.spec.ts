import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ILibraryMember } from 'app/entities/library-member/library-member.model';
import { LibraryMemberService } from 'app/entities/library-member/service/library-member.service';
import { IReservation } from '../reservation.model';
import { ReservationService } from '../service/reservation.service';

import { ReservationFormService } from './reservation-form.service';
import { ReservationUpdate } from './reservation-update';

describe('Reservation Management Update Component', () => {
  let comp: ReservationUpdate;
  let fixture: ComponentFixture<ReservationUpdate>;
  let activatedRoute: ActivatedRoute;
  let reservationFormService: ReservationFormService;
  let reservationService: ReservationService;
  let libraryMemberService: LibraryMemberService;
  let bookService: BookService;

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

    fixture = TestBed.createComponent(ReservationUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservationFormService = TestBed.inject(ReservationFormService);
    reservationService = TestBed.inject(ReservationService);
    libraryMemberService = TestBed.inject(LibraryMemberService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LibraryMember query and add missing value', () => {
      const reservation: IReservation = { id: 21991 };
      const member: ILibraryMember = { id: 8289 };
      reservation.member = member;

      const libraryMemberCollection: ILibraryMember[] = [{ id: 8289 }];
      vitest.spyOn(libraryMemberService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryMemberCollection })));
      const additionalLibraryMembers = [member];
      const expectedCollection: ILibraryMember[] = [...additionalLibraryMembers, ...libraryMemberCollection];
      vitest.spyOn(libraryMemberService, 'addLibraryMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(libraryMemberService.query).toHaveBeenCalled();
      expect(libraryMemberService.addLibraryMemberToCollectionIfMissing).toHaveBeenCalledWith(
        libraryMemberCollection,
        ...additionalLibraryMembers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.libraryMembersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Book query and add missing value', () => {
      const reservation: IReservation = { id: 21991 };
      const book: IBook = { id: 32624 };
      reservation.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      vitest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      vitest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.booksSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const reservation: IReservation = { id: 21991 };
      const member: ILibraryMember = { id: 8289 };
      reservation.member = member;
      const book: IBook = { id: 32624 };
      reservation.book = book;

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(comp.libraryMembersSharedCollection()).toContainEqual(member);
      expect(comp.booksSharedCollection()).toContainEqual(book);
      expect(comp.reservation).toEqual(reservation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IReservation>();
      const reservation = { id: 27139 };
      vitest.spyOn(reservationFormService, 'getReservation').mockReturnValue(reservation);
      vitest.spyOn(reservationService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(reservation);
      saveSubject.complete();

      // THEN
      expect(reservationFormService.getReservation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservationService.update).toHaveBeenCalledWith(expect.objectContaining(reservation));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IReservation>();
      const reservation = { id: 27139 };
      vitest.spyOn(reservationFormService, 'getReservation').mockReturnValue({ id: null });
      vitest.spyOn(reservationService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(reservation);
      saveSubject.complete();

      // THEN
      expect(reservationFormService.getReservation).toHaveBeenCalled();
      expect(reservationService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IReservation>();
      const reservation = { id: 27139 };
      vitest.spyOn(reservationService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservationService.update).toHaveBeenCalled();
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

    describe('compareBook', () => {
      it('should forward to bookService', () => {
        const entity = { id: 32624 };
        const entity2 = { id: 17120 };
        vitest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
