import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Periodo } from './periodo.model';

@Injectable({ providedIn: 'root' })
export class PeriodoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/periodos`;

  list(): Observable<Periodo[]> {
    return this.http.get<Periodo[]>(this.baseUrl);
  }

  create(ano: number, semestre: 1 | 2): Observable<Periodo> {
    return this.http.post<Periodo>(this.baseUrl, { ano, semestre });
  }

  atualizarConfig(
    id: string,
    escalaTotal: number,
    mediaMinimaPassar: number,
    mediaMinimaRecuperacao: number
  ): Observable<Periodo> {
    return this.http.put<Periodo>(`${this.baseUrl}/${id}/config`, {
      escalaTotal,
      mediaMinimaPassar,
      mediaMinimaRecuperacao
    });
  }
}
