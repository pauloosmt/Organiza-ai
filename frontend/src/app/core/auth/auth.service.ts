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

  esqueciSenha(email: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/esqueci-senha`, { email });
  }

  redefinirSenha(email: string, codigo: string, novaSenha: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/redefinir-senha`, { email, codigo, novaSenha });
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

  atualizarNome(name: string): Observable<User> {
    return this.http
      .put<User>(`${environment.apiUrl}/users/me`, { name })
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  atualizarTema(tema: 'light' | 'dark'): Observable<User> {
    return this.http
      .put<User>(`${environment.apiUrl}/users/me/tema`, { tema })
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  iniciarTrocaSenha(novaSenha: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/users/me/senha`, { novaSenha });
  }

  confirmarTrocaSenha(codigo: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/users/me/senha/confirmar`, { codigo });
  }

  reenviarCodigoTrocaSenha(): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/users/me/senha/reenviar`, {});
  }

  excluirConta(senha: string): Observable<void> {
    return this.http
      .delete<void>(`${environment.apiUrl}/users/me`, { body: { senha } })
      .pipe(tap(() => this.limparSessaoLocal()));
  }

  limparSessaoLocal(): void {
    this.currentUserSignal.set(null);
    this.periodoContext.resetar();
  }
}
