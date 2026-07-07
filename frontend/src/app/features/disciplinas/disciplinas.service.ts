import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Disciplina } from './disciplina.model';

@Injectable({ providedIn: 'root' })
export class DisciplinasService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/disciplinas`;

  list(periodoId: string): Observable<Disciplina[]> {
    const params = new HttpParams().set('periodoId', periodoId);
    return this.http.get<Disciplina[]>(this.baseUrl, { params });
  }

  create(nome: string, periodoId: string): Observable<Disciplina> {
    return this.http.post<Disciplina>(this.baseUrl, { nome, periodoId });
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  ajustarFaltas(id: string, delta: 1 | -1): Observable<Disciplina> {
    return this.http.patch<Disciplina>(`${this.baseUrl}/${id}/faltas`, { delta });
  }
}
