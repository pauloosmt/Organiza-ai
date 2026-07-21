import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';
import { tagColorVar } from '../../core/theme/tag-color';
import { formatarDataBr } from '../../core/utils/data-br';
import { DatePickerBr } from '../../shared/date-picker-br/date-picker-br';
import { Avaliacao } from '../avaliacoes/avaliacao.model';
import { AvaliacoesService } from '../avaliacoes/avaliacoes.service';
import { Disciplina } from './disciplina.model';
import { DisciplinasService } from './disciplinas.service';
import { atingiuLimiteFaltas, limiteFaltas } from './faltas-limite.util';
import { ResultadoMedia, calcularMedia } from './media.util';

@Component({
  selector: 'app-disciplinas',
  imports: [ReactiveFormsModule, DatePickerBr],
  templateUrl: './disciplinas.html',
  styleUrl: './disciplinas.scss'
})
export class Disciplinas implements OnInit {
  private readonly disciplinasService = inject(DisciplinasService);
  private readonly avaliacoesService = inject(AvaliacoesService);
  private readonly fb = inject(FormBuilder);
  readonly periodoContext = inject(PeriodoContextService);

  readonly disciplinas = signal<Disciplina[]>([]);
  readonly avaliacoes = signal<Avaliacao[]>([]);
  readonly errorMessage = signal<string | null>(null);

  readonly disciplinaSelecionada = signal<Disciplina | null>(null);
  readonly avaliacaoErrorMessage = signal<string | null>(null);

  readonly avaliacoesDaDisciplina = computed(() => {
    const disciplina = this.disciplinaSelecionada();
    if (!disciplina) {
      return [];
    }
    return this.avaliacoes()
      .filter((a) => a.disciplinaId === disciplina.id)
      .sort((a, b) => a.data.localeCompare(b.data));
  });

  readonly form = this.fb.group({
    nome: ['', [Validators.required]]
  });

  readonly avaliacaoForm = this.fb.group({
    titulo: ['', [Validators.required]],
    tipo: ['PROVA' as 'PROVA' | 'TRABALHO' | 'RECUPERACAO', [Validators.required]],
    data: ['', [Validators.required]],
    pontuacao: [10, [Validators.required, Validators.min(1)]]
  });

  readonly tagColorVar = tagColorVar;
  readonly formatarDataBr = formatarDataBr;
  readonly limiteFaltas = limiteFaltas;
  readonly atingiuLimiteFaltas = atingiuLimiteFaltas;

  readonly configPopupAberto = signal(false);
  readonly configForm = this.fb.group({
    escalaTotal: [10, [Validators.required, Validators.min(0)]],
    mediaMinimaPassar: [6, [Validators.required, Validators.min(0)]],
    mediaMinimaRecuperacao: [3, [Validators.required, Validators.min(0)]]
  });
  readonly configErro = signal<string | null>(null);
  readonly configSalvando = signal(false);

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

  cadastrar(): void {
    const periodo = this.periodoContext.periodoAtual();
    if (this.form.invalid || !periodo) {
      return;
    }
    const nome = this.form.getRawValue().nome!;
    this.disciplinasService.create(nome, periodo.id).subscribe({
      next: (disciplina) => {
        this.disciplinas.update((atual) => [...atual, disciplina]);
        this.form.reset();
      },
      error: () => this.errorMessage.set('Não foi possível cadastrar a disciplina.')
    });
  }

  remover(disciplina: Disciplina): void {
    this.disciplinasService.remove(disciplina.id).subscribe(() => {
      this.disciplinas.update((atual) => atual.filter((d) => d.id !== disciplina.id));
      this.avaliacoes.update((atual) => atual.filter((a) => a.disciplinaId !== disciplina.id));
    });
  }

  ajustarFaltas(disciplina: Disciplina, delta: 1 | -1): void {
    this.disciplinasService.ajustarFaltas(disciplina.id, delta).subscribe({
      next: (atualizada) => {
        this.disciplinas.update((atual) => atual.map((d) => (d.id === atualizada.id ? atualizada : d)));
      },
      error: () => this.errorMessage.set('Não foi possível ajustar as faltas.')
    });
  }

  abrirPainel(disciplina: Disciplina): void {
    this.disciplinaSelecionada.set(disciplina);
    this.avaliacaoErrorMessage.set(null);
    this.avaliacaoForm.reset({ titulo: '', tipo: 'PROVA', data: '', pontuacao: 10 });
  }

  fecharPainel(): void {
    this.disciplinaSelecionada.set(null);
  }

  cadastrarAvaliacao(): void {
    const disciplina = this.disciplinaSelecionada();
    if (this.avaliacaoForm.invalid || !disciplina) {
      return;
    }
    const valores = this.avaliacaoForm.getRawValue();
    this.avaliacoesService.create({
      disciplinaId: disciplina.id,
      titulo: valores.titulo!,
      tipo: valores.tipo!,
      data: valores.data!,
      pontuacao: valores.pontuacao!
    }).subscribe({
      next: (avaliacao) => {
        this.avaliacoes.update((atual) => [...atual, avaliacao]);
        this.avaliacaoForm.reset({ titulo: '', tipo: 'PROVA', data: '', pontuacao: 10 });
      },
      error: () => this.avaliacaoErrorMessage.set('Não foi possível cadastrar a avaliação.')
    });
  }

  atualizarNota(avaliacao: Avaliacao, notaTexto: string): void {
    const nota = notaTexto.trim() === '' ? null : Number(notaTexto);
    if (nota !== null && Number.isNaN(nota)) {
      return;
    }
    this.avaliacoesService.update(avaliacao.id, {
      titulo: avaliacao.titulo,
      tipo: avaliacao.tipo,
      data: avaliacao.data,
      pontuacao: avaliacao.pontuacao,
      nota
    }).subscribe({
      next: (atualizada) => {
        this.avaliacoes.update((atual) => atual.map((a) => (a.id === atualizada.id ? atualizada : a)));
      },
      error: () => this.avaliacaoErrorMessage.set('Não foi possível salvar a nota.')
    });
  }

  removerAvaliacao(avaliacao: Avaliacao): void {
    this.avaliacoesService.remove(avaliacao.id).subscribe(() => {
      this.avaliacoes.update((atual) => atual.filter((a) => a.id !== avaliacao.id));
    });
  }

  mediaDaDisciplina(disciplina: Disciplina): ResultadoMedia | null {
    const periodo = this.periodoContext.periodoAtual();
    if (!periodo) {
      return null;
    }
    const avaliacoesDaDisciplina = this.avaliacoes().filter((a) => a.disciplinaId === disciplina.id);
    return calcularMedia(periodo, avaliacoesDaDisciplina);
  }

  readonly mediaDaDisciplinaSelecionada = computed<ResultadoMedia | null>(() => {
    const disciplina = this.disciplinaSelecionada();
    const periodo = this.periodoContext.periodoAtual();
    if (!disciplina || !periodo) {
      return null;
    }
    return calcularMedia(periodo, this.avaliacoesDaDisciplina());
  });

  abrirConfigPopup(): void {
    const periodo = this.periodoContext.periodoAtual();
    if (!periodo) {
      return;
    }
    this.configErro.set(null);
    this.configForm.reset({
      escalaTotal: periodo.escalaTotal,
      mediaMinimaPassar: periodo.mediaMinimaPassar,
      mediaMinimaRecuperacao: periodo.mediaMinimaRecuperacao
    });
    this.configPopupAberto.set(true);
  }

  fecharConfigPopup(): void {
    this.configPopupAberto.set(false);
  }

  salvarConfig(): void {
    const periodo = this.periodoContext.periodoAtual();
    if (this.configForm.invalid || !periodo) {
      return;
    }
    const { escalaTotal, mediaMinimaPassar, mediaMinimaRecuperacao } = this.configForm.getRawValue();
    this.configErro.set(null);
    this.configSalvando.set(true);
    this.periodoContext.atualizarConfig(periodo.id, escalaTotal!, mediaMinimaPassar!, mediaMinimaRecuperacao!).subscribe({
      next: () => {
        this.configSalvando.set(false);
        this.configPopupAberto.set(false);
      },
      error: () => {
        this.configSalvando.set(false);
        this.configErro.set('Não foi possível salvar. Confira se os valores estão em ordem (recuperação ≤ passar ≤ escala).');
      }
    });
  }
}
