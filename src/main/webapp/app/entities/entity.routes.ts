import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'libraryManagementApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'library-member',
    data: { pageTitle: 'libraryManagementApp.libraryMember.home.title' },
    loadChildren: () => import('./library-member/library-member.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'libraryManagementApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'book',
    data: { pageTitle: 'libraryManagementApp.book.home.title' },
    loadChildren: () => import('./book/book.routes'),
  },
  {
    path: 'book-copy',
    data: { pageTitle: 'libraryManagementApp.bookCopy.home.title' },
    loadChildren: () => import('./book-copy/book-copy.routes'),
  },
  {
    path: 'loan',
    data: { pageTitle: 'libraryManagementApp.loan.home.title' },
    loadChildren: () => import('./loan/loan.routes'),
  },
  {
    path: 'reservation',
    data: { pageTitle: 'libraryManagementApp.reservation.home.title' },
    loadChildren: () => import('./reservation/reservation.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'libraryManagementApp.review.home.title' },
    loadChildren: () => import('./review/review.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
