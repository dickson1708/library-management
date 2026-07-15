import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LibraryMemberResolve from './route/library-member-routing-resolve.service';

const libraryMemberRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/library-member').then(m => m.LibraryMember),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/library-member-detail').then(m => m.LibraryMemberDetail),
    resolve: {
      libraryMember: LibraryMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/library-member-update').then(m => m.LibraryMemberUpdate),
    resolve: {
      libraryMember: LibraryMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/library-member-update').then(m => m.LibraryMemberUpdate),
    resolve: {
      libraryMember: LibraryMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default libraryMemberRoute;
