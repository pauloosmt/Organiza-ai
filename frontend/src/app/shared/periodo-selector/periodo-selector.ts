import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';

@Component({
  selector: 'app-periodo-selector',
  imports: [FormsModule],
  templateUrl: './periodo-selector.html',
  styleUrl: './periodo-selector.scss'
})
export class PeriodoSelector implements OnInit {
  readonly context = inject(PeriodoContextService);

  readonly criandoNovo = signal(false);
  readonly ano = signal(new Date().getFullYear());
  readonly semestre = signal<1 | 2>(1);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.context.carregar();
  }

  onSelecionar(id: string): void {
    if (id === '__novo__') {
      this.criandoNovo.set(true);
      return;
    }
    this.context.selecionar(id);
  }

  confirmarNovoPeriodo(): void {
    this.errorMessage.set(null);
    this.context.criar(this.ano(), this.semestre()).subscribe({
      next: () => this.criandoNovo.set(false),
      error: (err) => {
        this.errorMessage.set(
          err.status === 409 ? 'Esse período já existe.' : 'Não foi possível criar o período.'
        );
      }
    });
  }

  cancelarNovoPeriodo(): void {
    this.criandoNovo.set(false);
    this.errorMessage.set(null);
  }
}
