import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./shared/app-shell/app-shell').then((m) => m.AppShell),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home').then((m) => m.Home)
      },
      {
        path: 'disciplinas',
        loadComponent: () => import('./features/disciplinas/disciplinas').then((m) => m.Disciplinas)
      },
      {
        path: 'grade',
        loadComponent: () => import('./features/grade/grade-board').then((m) => m.GradeBoard)
      },
      {
        path: 'premium',
        loadComponent: () => import('./features/premium/premium').then((m) => m.Premium)
      }
    ]
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register)
  },
  {
    path: 'verificar-email',
    loadComponent: () => import('./features/auth/verificar-email/verificar-email').then((m) => m.VerificarEmail)
  },
  { path: '**', redirectTo: '' }
];
