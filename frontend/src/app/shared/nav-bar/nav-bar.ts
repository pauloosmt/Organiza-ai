import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ThemeService } from '../../core/theme/theme.service';
import { BrandEmblem } from '../brand-emblem/brand-emblem';
import { PeriodoSelector } from '../periodo-selector/periodo-selector';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterLink, RouterLinkActive, PeriodoSelector, BrandEmblem],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss'
})
export class NavBar {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly themeService = inject(ThemeService);

  logout(): void {
    this.authService.logout().subscribe(() => this.router.navigateByUrl('/login'));
  }
}
