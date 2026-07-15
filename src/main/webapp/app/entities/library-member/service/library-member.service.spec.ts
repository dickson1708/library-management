import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILibraryMember } from '../library-member.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../library-member.test-samples';

import { LibraryMemberService, RestLibraryMember } from './library-member.service';

const requireRestSample: RestLibraryMember = {
  ...sampleWithRequiredData,
  birthDate: sampleWithRequiredData.birthDate?.format(DATE_FORMAT),
  registrationDate: sampleWithRequiredData.registrationDate?.format(DATE_FORMAT),
};

describe('LibraryMember Service', () => {
  let service: LibraryMemberService;
  let httpMock: HttpTestingController;
  let expectedResult: ILibraryMember | ILibraryMember[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LibraryMemberService);
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

    it('should create a LibraryMember', () => {
      const libraryMember = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(libraryMember).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LibraryMember', () => {
      const libraryMember = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(libraryMember).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LibraryMember', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LibraryMember', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LibraryMember', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLibraryMemberToCollectionIfMissing', () => {
      it('should add a LibraryMember to an empty array', () => {
        const libraryMember: ILibraryMember = sampleWithRequiredData;
        expectedResult = service.addLibraryMemberToCollectionIfMissing([], libraryMember);
        expect(expectedResult).toEqual([libraryMember]);
      });

      it('should not add a LibraryMember to an array that contains it', () => {
        const libraryMember: ILibraryMember = sampleWithRequiredData;
        const libraryMemberCollection: ILibraryMember[] = [
          {
            ...libraryMember,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLibraryMemberToCollectionIfMissing(libraryMemberCollection, libraryMember);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LibraryMember to an array that doesn't contain it", () => {
        const libraryMember: ILibraryMember = sampleWithRequiredData;
        const libraryMemberCollection: ILibraryMember[] = [sampleWithPartialData];
        expectedResult = service.addLibraryMemberToCollectionIfMissing(libraryMemberCollection, libraryMember);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(libraryMember);
      });

      it('should add only unique LibraryMember to an array', () => {
        const libraryMemberArray: ILibraryMember[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const libraryMemberCollection: ILibraryMember[] = [sampleWithRequiredData];
        expectedResult = service.addLibraryMemberToCollectionIfMissing(libraryMemberCollection, ...libraryMemberArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const libraryMember: ILibraryMember = sampleWithRequiredData;
        const libraryMember2: ILibraryMember = sampleWithPartialData;
        expectedResult = service.addLibraryMemberToCollectionIfMissing([], libraryMember, libraryMember2);
        expect(expectedResult).toEqual([libraryMember, libraryMember2]);
      });

      it('should accept null and undefined values', () => {
        const libraryMember: ILibraryMember = sampleWithRequiredData;
        expectedResult = service.addLibraryMemberToCollectionIfMissing([], null, libraryMember, undefined);
        expect(expectedResult).toEqual([libraryMember]);
      });

      it('should return initial array if no LibraryMember is added', () => {
        const libraryMemberCollection: ILibraryMember[] = [sampleWithRequiredData];
        expectedResult = service.addLibraryMemberToCollectionIfMissing(libraryMemberCollection, undefined, null);
        expect(expectedResult).toEqual(libraryMemberCollection);
      });
    });

    describe('compareLibraryMember', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLibraryMember(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8289 };
        const entity2 = null;

        const compareResult1 = service.compareLibraryMember(entity1, entity2);
        const compareResult2 = service.compareLibraryMember(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8289 };
        const entity2 = { id: 13667 };

        const compareResult1 = service.compareLibraryMember(entity1, entity2);
        const compareResult2 = service.compareLibraryMember(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8289 };
        const entity2 = { id: 8289 };

        const compareResult1 = service.compareLibraryMember(entity1, entity2);
        const compareResult2 = service.compareLibraryMember(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
