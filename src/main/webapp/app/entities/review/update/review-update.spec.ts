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
import { IReview } from '../review.model';
import { ReviewService } from '../service/review.service';

import { ReviewFormService } from './review-form.service';
import { ReviewUpdate } from './review-update';

describe('Review Management Update Component', () => {
  let comp: ReviewUpdate;
  let fixture: ComponentFixture<ReviewUpdate>;
  let activatedRoute: ActivatedRoute;
  let reviewFormService: ReviewFormService;
  let reviewService: ReviewService;
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

    fixture = TestBed.createComponent(ReviewUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reviewFormService = TestBed.inject(ReviewFormService);
    reviewService = TestBed.inject(ReviewService);
    libraryMemberService = TestBed.inject(LibraryMemberService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LibraryMember query and add missing value', () => {
      const review: IReview = { id: 8996 };
      const member: ILibraryMember = { id: 8289 };
      review.member = member;

      const libraryMemberCollection: ILibraryMember[] = [{ id: 8289 }];
      vitest.spyOn(libraryMemberService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryMemberCollection })));
      const additionalLibraryMembers = [member];
      const expectedCollection: ILibraryMember[] = [...additionalLibraryMembers, ...libraryMemberCollection];
      vitest.spyOn(libraryMemberService, 'addLibraryMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ review });
      comp.ngOnInit();

      expect(libraryMemberService.query).toHaveBeenCalled();
      expect(libraryMemberService.addLibraryMemberToCollectionIfMissing).toHaveBeenCalledWith(
        libraryMemberCollection,
        ...additionalLibraryMembers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.libraryMembersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Book query and add missing value', () => {
      const review: IReview = { id: 8996 };
      const book: IBook = { id: 32624 };
      review.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      vitest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      vitest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ review });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.booksSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const review: IReview = { id: 8996 };
      const member: ILibraryMember = { id: 8289 };
      review.member = member;
      const book: IBook = { id: 32624 };
      review.book = book;

      activatedRoute.data = of({ review });
      comp.ngOnInit();

      expect(comp.libraryMembersSharedCollection()).toContainEqual(member);
      expect(comp.booksSharedCollection()).toContainEqual(book);
      expect(comp.review).toEqual(review);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IReview>();
      const review = { id: 21337 };
      vitest.spyOn(reviewFormService, 'getReview').mockReturnValue(review);
      vitest.spyOn(reviewService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ review });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(review);
      saveSubject.complete();

      // THEN
      expect(reviewFormService.getReview).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reviewService.update).toHaveBeenCalledWith(expect.objectContaining(review));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IReview>();
      const review = { id: 21337 };
      vitest.spyOn(reviewFormService, 'getReview').mockReturnValue({ id: null });
      vitest.spyOn(reviewService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ review: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(review);
      saveSubject.complete();

      // THEN
      expect(reviewFormService.getReview).toHaveBeenCalled();
      expect(reviewService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IReview>();
      const review = { id: 21337 };
      vitest.spyOn(reviewService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ review });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reviewService.update).toHaveBeenCalled();
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
