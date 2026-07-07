import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateOuUpdateGradeBlocoPayload, GradeBloco } from './grade-bloco.model';

@Injectable({ providedIn: 'root' })
export class GradeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/grade/blocos`;

  list(): Observable<GradeBloco[]> {
    return this.http.get<GradeBloco[]>(this.baseUrl);
  }

  create(payload: CreateOuUpdateGradeBlocoPayload): Observable<GradeBloco> {
    return this.http.post<GradeBloco>(this.baseUrl, payload);
  }

  update(id: string, payload: CreateOuUpdateGradeBlocoPayload): Observable<GradeBloco> {
    return this.http.put<GradeBloco>(`${this.baseUrl}/${id}`, payload);
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
