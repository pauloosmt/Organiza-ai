import { HttpErrorResponse } from '@angular/common/http';
import { Component, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { timer } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { Sidebar } from './sidebar/sidebar';
import { Topbar } from './topbar/topbar';
import { PeriodoObrigatorioModal } from '../periodo-obrigatorio-modal/periodo-obrigatorio-modal';

const TENTATIVAS_VERIFICACAO_SESSAO = 2;
const INTERVALO_ENTRE_TENTATIVAS_MS = 2000;

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, Sidebar, Topbar, PeriodoObrigatorioModal],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss'
})
export class AppShell {
  private readonly authService = inject(AuthService);

  @HostListener('document:visibilitychange')
  onVisibilityChange(): void {
    if (document.visibilityState === 'visible') {
      this.verificarSessao(TENTATIVAS_VERIFICACAO_SESSAO);
    }
  }

  private verificarSessao(tentativasRestantes: number): void {
    this.authService.fetchCurrentUser().subscribe({
      error: (error) => {
        // 401 já foi tratado pelo session-expired.interceptor (limpou a
        // sessão e navegou pro login) — só vale insistir quando a falha é de
        // rede/timeout (ex: backend acordando de um cold start no Render),
        // que não diz nada sobre a sessão em si.
        const falhaDeRede = !(error instanceof HttpErrorResponse) || error.status !== 401;
        if (falhaDeRede && tentativasRestantes > 1) {
          timer(INTERVALO_ENTRE_TENTATIVAS_MS).subscribe(() => this.verificarSessao(tentativasRestantes - 1));
        }
      }
    });
  }
}
