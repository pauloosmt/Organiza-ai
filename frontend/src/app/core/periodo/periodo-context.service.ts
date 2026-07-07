import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Periodo } from './periodo.model';
import { PeriodoService } from './periodo.service';

@Injectable({ providedIn: 'root' })
export class PeriodoContextService {
  private readonly periodoService = inject(PeriodoService);

  readonly periodos = signal<Periodo[]>([]);
  private readonly periodoAtualId = signal<string | null>(null);

  readonly periodoAtual = computed(
    () => this.periodos().find((p) => p.id === this.periodoAtualId()) ?? null
  );

  carregar(): void {
    if (this.periodos().length > 0) {
      return;
    }
    this.periodoService.list().subscribe((periodos) => {
      this.periodos.set(periodos);
      if (periodos.length > 0) {
        this.periodoAtualId.set(periodos[0].id);
      }
    });
  }

  selecionar(id: string): void {
    this.periodoAtualId.set(id);
  }

  criar(ano: number, semestre: 1 | 2): Observable<Periodo> {
    return this.periodoService.create(ano, semestre).pipe(
      tap((periodo) => {
        this.periodos.update((atual) => [periodo, ...atual]);
        this.periodoAtualId.set(periodo.id);
      })
    );
  }
}
