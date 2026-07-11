import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PeriodoContextService } from '../periodo/periodo-context.service';
import { User } from './user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly periodoContext = inject(PeriodoContextService);
  private readonly currentUserSignal = signal<User | null>(null);
  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor(private readonly http: HttpClient) {}

  register(name: string, email: string, password: string): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/auth/register`, { name, email, password });
  }

  verificarEmail(email: string, codigo: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/verificar-email`, { email, codigo });
  }

  reenviarCodigo(email: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/reenviar-codigo`, { email });
  }

  login(email: string, password: string): Observable<User> {
    return this.http
      .post<User>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/logout`, {}).pipe(
      tap(() => this.limparSessaoLocal())
    );
  }

  fetchCurrentUser(): Observable<User> {
    return this.http
      .get<User>(`${environment.apiUrl}/users/me`)
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  limparSessaoLocal(): void {
    this.currentUserSignal.set(null);
    this.periodoContext.resetar();
  }
}
