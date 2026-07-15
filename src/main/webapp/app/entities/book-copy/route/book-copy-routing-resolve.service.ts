import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IBookCopy } from '../book-copy.model';
import { BookCopyService } from '../service/book-copy.service';

const bookCopyResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookCopy> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(BookCopyService);
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

export default bookCopyResolve;
