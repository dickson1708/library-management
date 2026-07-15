import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BookCopyResolve from './route/book-copy-routing-resolve.service';

const bookCopyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/book-copy').then(m => m.BookCopy),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/book-copy-detail').then(m => m.BookCopyDetail),
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/book-copy-update').then(m => m.BookCopyUpdate),
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/book-copy-update').then(m => m.BookCopyUpdate),
    resolve: {
      bookCopy: BookCopyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookCopyRoute;
