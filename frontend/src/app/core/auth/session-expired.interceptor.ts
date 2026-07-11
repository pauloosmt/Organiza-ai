import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

const ROTAS_PUBLICAS_DE_AUTH = [
  '/auth/login',
  '/auth/register',
  '/auth/verificar-email',
  '/auth/reenviar-codigo'
];

export const sessionExpiredInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const ehRotaPublicaDeAuth = ROTAS_PUBLICAS_DE_AUTH.some((rota) => req.url.includes(rota));

  return next(req).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && !ehRotaPublicaDeAuth) {
        authService.limparSessaoLocal();
        router.navigateByUrl('/login');
      }
      return throwError(() => error);
    })
  );
};
