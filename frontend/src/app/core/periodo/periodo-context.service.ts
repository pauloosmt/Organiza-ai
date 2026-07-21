import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Periodo } from './periodo.model';
import { PeriodoService } from './periodo.service';

@Injectable({ providedIn: 'root' })
export class PeriodoContextService {
  private readonly periodoService = inject(PeriodoService);

  readonly periodos = signal<Periodo[]>([]);
  readonly carregado = signal(false);
  private readonly periodoAtualId = signal<string | null>(null);

  readonly periodoAtual = computed(
    () => this.periodos().find((p) => p.id === this.periodoAtualId()) ?? null
  );

  readonly precisaCriarPeriodo = computed(
    () => this.carregado() && this.periodos().length === 0
  );

  carregar(): void {
    if (this.carregado()) {
      return;
    }
    this.periodoService.list().subscribe((periodos) => {
      this.periodos.set(periodos);
      if (periodos.length > 0) {
        this.periodoAtualId.set(periodos[0].id);
      }
      this.carregado.set(true);
    });
  }

  selecionar(id: string): void {
    this.periodoAtualId.set(id);
  }

  resetar(): void {
    this.periodos.set([]);
    this.periodoAtualId.set(null);
    this.carregado.set(false);
  }

  criar(ano: number, semestre: 1 | 2): Observable<Periodo> {
    return this.periodoService.create(ano, semestre).pipe(
      tap((periodo) => {
        this.periodos.update((atual) => [periodo, ...atual]);
        this.periodoAtualId.set(periodo.id);
      })
    );
  }

  atualizarConfig(
    id: string,
    escalaTotal: number,
    mediaMinimaPassar: number,
    mediaMinimaRecuperacao: number
  ): Observable<Periodo> {
    return this.periodoService.atualizarConfig(id, escalaTotal, mediaMinimaPassar, mediaMinimaRecuperacao).pipe(
      tap((periodo) => {
        this.periodos.update((atual) => atual.map((p) => (p.id === periodo.id ? periodo : p)));
      })
    );
  }
}
