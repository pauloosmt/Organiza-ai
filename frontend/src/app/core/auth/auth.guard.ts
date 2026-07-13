import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { ThemeService } from '../theme/theme.service';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const themeService = inject(ThemeService);
  const router = inject(Router);

  return authService.fetchCurrentUser().pipe(
    map((user) => {
      themeService.aplicarTemaDaConta(user.tema);
      return true;
    }),
    catchError(() => of(router.parseUrl('/login')))
  );
};
