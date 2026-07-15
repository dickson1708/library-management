import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBookCopy } from '../book-copy.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../book-copy.test-samples';

import { BookCopyService, RestBookCopy } from './book-copy.service';

const requireRestSample: RestBookCopy = {
  ...sampleWithRequiredData,
  acquisitionDate: sampleWithRequiredData.acquisitionDate?.format(DATE_FORMAT),
};

describe('BookCopy Service', () => {
  let service: BookCopyService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookCopy | IBookCopy[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookCopyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BookCopy', () => {
      const bookCopy = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookCopy).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookCopy', () => {
      const bookCopy = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookCopy).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookCopy', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookCopy', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookCopy', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addBookCopyToCollectionIfMissing', () => {
      it('should add a BookCopy to an empty array', () => {
        const bookCopy: IBookCopy = sampleWithRequiredData;
        expectedResult = service.addBookCopyToCollectionIfMissing([], bookCopy);
        expect(expectedResult).toEqual([bookCopy]);
      });

      it('should not add a BookCopy to an array that contains it', () => {
        const bookCopy: IBookCopy = sampleWithRequiredData;
        const bookCopyCollection: IBookCopy[] = [
          {
            ...bookCopy,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookCopyToCollectionIfMissing(bookCopyCollection, bookCopy);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookCopy to an array that doesn't contain it", () => {
        const bookCopy: IBookCopy = sampleWithRequiredData;
        const bookCopyCollection: IBookCopy[] = [sampleWithPartialData];
        expectedResult = service.addBookCopyToCollectionIfMissing(bookCopyCollection, bookCopy);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookCopy);
      });

      it('should add only unique BookCopy to an array', () => {
        const bookCopyArray: IBookCopy[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookCopyCollection: IBookCopy[] = [sampleWithRequiredData];
        expectedResult = service.addBookCopyToCollectionIfMissing(bookCopyCollection, ...bookCopyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookCopy: IBookCopy = sampleWithRequiredData;
        const bookCopy2: IBookCopy = sampleWithPartialData;
        expectedResult = service.addBookCopyToCollectionIfMissing([], bookCopy, bookCopy2);
        expect(expectedResult).toEqual([bookCopy, bookCopy2]);
      });

      it('should accept null and undefined values', () => {
        const bookCopy: IBookCopy = sampleWithRequiredData;
        expectedResult = service.addBookCopyToCollectionIfMissing([], null, bookCopy, undefined);
        expect(expectedResult).toEqual([bookCopy]);
      });

      it('should return initial array if no BookCopy is added', () => {
        const bookCopyCollection: IBookCopy[] = [sampleWithRequiredData];
        expectedResult = service.addBookCopyToCollectionIfMissing(bookCopyCollection, undefined, null);
        expect(expectedResult).toEqual(bookCopyCollection);
      });
    });

    describe('compareBookCopy', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookCopy(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11646 };
        const entity2 = null;

        const compareResult1 = service.compareBookCopy(entity1, entity2);
        const compareResult2 = service.compareBookCopy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11646 };
        const entity2 = { id: 4928 };

        const compareResult1 = service.compareBookCopy(entity1, entity2);
        const compareResult2 = service.compareBookCopy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11646 };
        const entity2 = { id: 11646 };

        const compareResult1 = service.compareBookCopy(entity1, entity2);
        const compareResult2 = service.compareBookCopy(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
