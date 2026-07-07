import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NavBar } from '../../shared/nav-bar/nav-bar';
import { Disciplina } from './disciplina.model';
import { DisciplinasService } from './disciplinas.service';

@Component({
  selector: 'app-disciplinas',
  imports: [ReactiveFormsModule, NavBar],
  templateUrl: './disciplinas.html',
  styleUrl: './disciplinas.scss'
})
export class Disciplinas implements OnInit {
  private readonly disciplinasService = inject(DisciplinasService);
  private readonly fb = inject(FormBuilder);

  readonly disciplinas = signal<Disciplina[]>([]);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    nome: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.disciplinasService.list().subscribe((disciplinas) => this.disciplinas.set(disciplinas));
  }

  cadastrar(): void {
    if (this.form.invalid) {
      return;
    }
    const nome = this.form.getRawValue().nome!;
    this.disciplinasService.create(nome).subscribe({
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
