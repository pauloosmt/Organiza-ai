import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Avaliacao, CreateAvaliacaoPayload, UpdateAvaliacaoPayload } from './avaliacao.model';

@Injectable({ providedIn: 'root' })
export class AvaliacoesService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/avaliacoes`;

  list(periodoId: string): Observable<Avaliacao[]> {
    const params = new HttpParams().set('periodoId', periodoId);
    return this.http.get<Avaliacao[]>(this.baseUrl, { params });
  }

  create(payload: CreateAvaliacaoPayload): Observable<Avaliacao> {
    return this.http.post<Avaliacao>(this.baseUrl, payload);
  }

  update(id: string, payload: UpdateAvaliacaoPayload): Observable<Avaliacao> {
    return this.http.patch<Avaliacao>(`${this.baseUrl}/${id}`, payload);
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
