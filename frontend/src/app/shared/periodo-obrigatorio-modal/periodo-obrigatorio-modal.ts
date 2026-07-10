import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';

@Component({
  selector: 'app-periodo-obrigatorio-modal',
  imports: [FormsModule],
  templateUrl: './periodo-obrigatorio-modal.html',
  styleUrl: './periodo-obrigatorio-modal.scss'
})
export class PeriodoObrigatorioModal {
  readonly context = inject(PeriodoContextService);

  readonly ano = signal(new Date().getFullYear());
  readonly semestre = signal<1 | 2>(1);
  readonly errorMessage = signal<string | null>(null);
  readonly salvando = signal(false);

  constructor() {
    this.context.carregar();
  }

  criar(): void {
    this.errorMessage.set(null);
    this.salvando.set(true);
    this.context.criar(this.ano(), this.semestre()).subscribe({
      next: () => this.salvando.set(false),
      error: (err) => {
        this.salvando.set(false);
        this.errorMessage.set(
          err.status === 409 ? 'Esse período já existe.' : 'Não foi possível criar o período.'
        );
      }
    });
  }
}
