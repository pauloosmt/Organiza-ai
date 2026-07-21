import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { PeriodoContextService } from '../../../core/periodo/periodo-context.service';
import { Disciplina } from '../../disciplinas/disciplina.model';
import { DisciplinasService } from '../../disciplinas/disciplinas.service';
import { AvaliacaoForm } from '../avaliacao-form/avaliacao-form';
import { AvaliacaoLinha } from '../avaliacao-linha/avaliacao-linha';
import { Avaliacao } from '../avaliacao.model';
import { AvaliacoesService } from '../avaliacoes.service';

@Component({
  selector: 'app-avaliacoes-page',
  imports: [AvaliacaoForm, AvaliacaoLinha],
  templateUrl: './avaliacoes-page.html',
  styleUrl: './avaliacoes-page.scss'
})
export class AvaliacoesPage implements OnInit {
  private readonly disciplinasService = inject(DisciplinasService);
  private readonly avaliacoesService = inject(AvaliacoesService);
  readonly periodoContext = inject(PeriodoContextService);

  readonly disciplinas = signal<Disciplina[]>([]);
  readonly avaliacoes = signal<Avaliacao[]>([]);
  readonly disciplinaSelecionadaId = signal<string | null>(null);

  readonly avaliacoesOrdenadas = computed(() =>
    [...this.avaliacoes()].sort((a, b) => a.data.localeCompare(b.data))
  );

  constructor() {
    effect(() => {
      const periodo = this.periodoContext.periodoAtual();
      if (periodo) {
        this.carregar(periodo.id);
      } else {
        this.disciplinas.set([]);
        this.avaliacoes.set([]);
      }
    });
  }

  ngOnInit(): void {
    this.periodoContext.carregar();
  }

  carregar(periodoId: string): void {
    this.disciplinasService.list(periodoId).subscribe((disciplinas) => this.disciplinas.set(disciplinas));
    this.avaliacoesService.list(periodoId).subscribe((avaliacoes) => this.avaliacoes.set(avaliacoes));
  }

  selecionarDisciplina(id: string): void {
    this.disciplinaSelecionadaId.set(id || null);
  }

  aoCadastrarAvaliacao(avaliacao: Avaliacao): void {
    this.avaliacoes.update((atual) => [...atual, avaliacao]);
  }

  aoAtualizarAvaliacao(avaliacao: Avaliacao): void {
    this.avaliacoes.update((atual) => atual.map((a) => (a.id === avaliacao.id ? avaliacao : a)));
  }

  aoRemoverAvaliacao(avaliacao: Avaliacao): void {
    this.avaliacoes.update((atual) => atual.filter((a) => a.id !== avaliacao.id));
  }
}
