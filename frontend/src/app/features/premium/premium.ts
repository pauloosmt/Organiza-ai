import { Component, OnInit, inject, signal } from '@angular/core';
import { PremiumInterestService } from './premium-interest.service';

@Component({
  selector: 'app-premium',
  imports: [],
  templateUrl: './premium.html',
  styleUrl: './premium.scss'
})
export class Premium implements OnInit {
  private readonly premiumInterestService = inject(PremiumInterestService);

  readonly registrado = signal(false);
  readonly carregando = signal(false);

  readonly beneficios = [
    {
      titulo: 'Histórico de períodos',
      descricao: 'Acesse disciplinas, grades e faltas de todos os períodos anteriores, sem limite.'
    },
    {
      titulo: 'Exportação em PDF ilimitada',
      descricao: 'Gere quantos PDFs quiser da sua grade e da sua agenda de provas.'
    },
    {
      titulo: 'Lembretes por e-mail',
      descricao: 'Receba um aviso antes de provas, trabalhos e quando estiver perto do limite de faltas.'
    },
    {
      titulo: 'Temas exclusivos',
      descricao: 'Personalize as cores do app além do padrão.'
    }
  ];

  ngOnInit(): void {
    this.premiumInterestService.status().subscribe((status) => this.registrado.set(status.registrado));
  }

  registrarInteresse(): void {
    if (this.registrado() || this.carregando()) {
      return;
    }
    this.carregando.set(true);
    this.premiumInterestService.register().subscribe({
      next: (status) => {
        this.registrado.set(status.registrado);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });
  }
}
