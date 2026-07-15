import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';

import { BookCopyFormService } from './book-copy-form.service';
import { BookCopyUpdate } from './book-copy-update';

describe('BookCopy Management Update Component', () => {
  let comp: BookCopyUpdate;
  let fixture: ComponentFixture<BookCopyUpdate>;
  let activatedRoute: ActivatedRoute;
  let bookCopyFormService: BookCopyFormService;
  let bookCopyService: BookCopyService;
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

    fixture = TestBed.createComponent(BookCopyUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookCopyFormService = TestBed.inject(BookCopyFormService);
    bookCopyService = TestBed.inject(BookCopyService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Book query and add missing value', () => {
      const bookCopy: IBookCopy = { id: 4928 };
      const book: IBook = { id: 32624 };
      bookCopy.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      vitest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      vitest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.booksSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const bookCopy: IBookCopy = { id: 4928 };
      const book: IBook = { id: 32624 };
      bookCopy.book = book;

      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      expect(comp.booksSharedCollection()).toContainEqual(book);
      expect(comp.bookCopy).toEqual(bookCopy);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBookCopy>();
      const bookCopy = { id: 11646 };
      vitest.spyOn(bookCopyFormService, 'getBookCopy').mockReturnValue(bookCopy);
      vitest.spyOn(bookCopyService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bookCopy);
      saveSubject.complete();

      // THEN
      expect(bookCopyFormService.getBookCopy).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookCopyService.update).toHaveBeenCalledWith(expect.objectContaining(bookCopy));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBookCopy>();
      const bookCopy = { id: 11646 };
      vitest.spyOn(bookCopyFormService, 'getBookCopy').mockReturnValue({ id: null });
      vitest.spyOn(bookCopyService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bookCopy);
      saveSubject.complete();

      // THEN
      expect(bookCopyFormService.getBookCopy).toHaveBeenCalled();
      expect(bookCopyService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBookCopy>();
      const bookCopy = { id: 11646 };
      vitest.spyOn(bookCopyService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCopy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookCopyService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
