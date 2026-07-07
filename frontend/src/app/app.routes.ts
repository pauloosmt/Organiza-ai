import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
    canActivate: [authGuard]
  },
  {
    path: 'disciplinas',
    loadComponent: () => import('./features/disciplinas/disciplinas').then((m) => m.Disciplinas),
    canActivate: [authGuard]
  },
  {
    path: 'grade',
    loadComponent: () => import('./features/grade/grade-board').then((m) => m.GradeBoard),
    canActivate: [authGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register)
  },
  { path: '**', redirectTo: '' }
];
