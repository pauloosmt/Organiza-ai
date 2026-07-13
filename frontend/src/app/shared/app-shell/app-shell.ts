import { Component, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Sidebar } from './sidebar/sidebar';
import { Topbar } from './topbar/topbar';
import { PeriodoObrigatorioModal } from '../periodo-obrigatorio-modal/periodo-obrigatorio-modal';

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
      this.authService.fetchCurrentUser().subscribe({ error: () => {} });
    }
  }
}
