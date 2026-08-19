import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { PeriodoContextService } from '../../../core/periodo/periodo-context.service';
import { tagColorVar } from '../../../core/theme/tag-color';
import { Disciplina } from '../../disciplinas/disciplina.model';
import { DisciplinasService } from '../../disciplinas/disciplinas.service';
import { AvaliacaoForm } from '../avaliacao-form/avaliacao-form';
import { AvaliacaoLinha } from '../avaliacao-linha/avaliacao-linha';
import { Avaliacao } from '../avaliacao.model';
import { AvaliacoesService } from '../avaliacoes.service';

interface GrupoDisciplina {
  disciplina: Disciplina;
  avaliacoes: Avaliacao[];
}

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
  readonly gruposColapsados = signal<Set<string>>(new Set());

  readonly tagColorVar = tagColorVar;

  readonly avaliacoesOrdenadas = computed(() =>
    [...this.avaliacoes()].sort((a, b) => a.data.localeCompare(b.data))
  );

  readonly grupos = computed<GrupoDisciplina[]>(() => {
    const porDisciplina = new Map<string, Avaliacao[]>();
    for (const avaliacao of this.avaliacoesOrdenadas()) {
      const lista = porDisciplina.get(avaliacao.disciplinaId) ?? [];
      lista.push(avaliacao);
      porDisciplina.set(avaliacao.disciplinaId, lista);
    }
    return this.disciplinas()
      .map((disciplina) => ({ disciplina, avaliacoes: porDisciplina.get(disciplina.id) ?? [] }))
      .filter((grupo) => grupo.avaliacoes.length > 0);
  });

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

  alternarGrupo(disciplinaId: string): void {
    this.gruposColapsados.update((atual) => {
      const novo = new Set(atual);
      if (novo.has(disciplinaId)) {
        novo.delete(disciplinaId);
      } else {
        novo.add(disciplinaId);
      }
      return novo;
    });
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
