import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { ILibraryMember } from '../library-member.model';
import { LibraryMemberService } from '../service/library-member.service';

import { LibraryMemberFormService } from './library-member-form.service';
import { LibraryMemberUpdate } from './library-member-update';

describe('LibraryMember Management Update Component', () => {
  let comp: LibraryMemberUpdate;
  let fixture: ComponentFixture<LibraryMemberUpdate>;
  let activatedRoute: ActivatedRoute;
  let libraryMemberFormService: LibraryMemberFormService;
  let libraryMemberService: LibraryMemberService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(LibraryMemberUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    libraryMemberFormService = TestBed.inject(LibraryMemberFormService);
    libraryMemberService = TestBed.inject(LibraryMemberService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const libraryMember: ILibraryMember = { id: 13667 };
      const internalUser: IUser = { id: 3944 };
      libraryMember.internalUser = internalUser;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [internalUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ libraryMember });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const libraryMember: ILibraryMember = { id: 13667 };
      const internalUser: IUser = { id: 3944 };
      libraryMember.internalUser = internalUser;

      activatedRoute.data = of({ libraryMember });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(internalUser);
      expect(comp.libraryMember).toEqual(libraryMember);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILibraryMember>();
      const libraryMember = { id: 8289 };
      vitest.spyOn(libraryMemberFormService, 'getLibraryMember').mockReturnValue(libraryMember);
      vitest.spyOn(libraryMemberService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryMember });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(libraryMember);
      saveSubject.complete();

      // THEN
      expect(libraryMemberFormService.getLibraryMember).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(libraryMemberService.update).toHaveBeenCalledWith(expect.objectContaining(libraryMember));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILibraryMember>();
      const libraryMember = { id: 8289 };
      vitest.spyOn(libraryMemberFormService, 'getLibraryMember').mockReturnValue({ id: null });
      vitest.spyOn(libraryMemberService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryMember: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(libraryMember);
      saveSubject.complete();

      // THEN
      expect(libraryMemberFormService.getLibraryMember).toHaveBeenCalled();
      expect(libraryMemberService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILibraryMember>();
      const libraryMember = { id: 8289 };
      vitest.spyOn(libraryMemberService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryMember });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(libraryMemberService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
