import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';
import { tagColorVar } from '../../core/theme/tag-color';
import { Disciplina } from './disciplina.model';
import { DisciplinasService } from './disciplinas.service';

@Component({
  selector: 'app-disciplinas',
  imports: [ReactiveFormsModule],
  templateUrl: './disciplinas.html',
  styleUrl: './disciplinas.scss'
})
export class Disciplinas implements OnInit {
  private readonly disciplinasService = inject(DisciplinasService);
  private readonly fb = inject(FormBuilder);
  readonly periodoContext = inject(PeriodoContextService);

  readonly disciplinas = signal<Disciplina[]>([]);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    nome: ['', [Validators.required]]
  });

  readonly tagColorVar = tagColorVar;

  constructor() {
    effect(() => {
      const periodo = this.periodoContext.periodoAtual();
      if (periodo) {
        this.carregar(periodo.id);
      } else {
        this.disciplinas.set([]);
      }
    });
  }

  ngOnInit(): void {
    this.periodoContext.carregar();
  }

  carregar(periodoId: string): void {
    this.disciplinasService.list(periodoId).subscribe((disciplinas) => this.disciplinas.set(disciplinas));
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
}
