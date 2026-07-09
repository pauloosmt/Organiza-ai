import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { ColdStartService } from './cold-start.service';

export const coldStartInterceptor: HttpInterceptorFn = (req, next) => {
  const coldStart = inject(ColdStartService);
  coldStart.iniciarRequisicao();
  return next(req).pipe(finalize(() => coldStart.finalizarRequisicao()));
};
