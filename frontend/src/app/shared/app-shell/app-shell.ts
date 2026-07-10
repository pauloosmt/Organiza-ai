import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from './sidebar/sidebar';
import { Topbar } from './topbar/topbar';
import { PeriodoObrigatorioModal } from '../periodo-obrigatorio-modal/periodo-obrigatorio-modal';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, Sidebar, Topbar, PeriodoObrigatorioModal],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss'
})
export class AppShell {}
