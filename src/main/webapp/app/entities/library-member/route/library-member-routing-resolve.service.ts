import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ILibraryMember } from '../library-member.model';
import { LibraryMemberService } from '../service/library-member.service';

const libraryMemberResolve = (route: ActivatedRouteSnapshot): Observable<null | ILibraryMember> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(LibraryMemberService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default libraryMemberResolve;
