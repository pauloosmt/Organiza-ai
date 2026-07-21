import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePickerBr } from '../../../shared/date-picker-br/date-picker-br';
import { Avaliacao, TipoAvaliacao } from '../avaliacao.model';
import { AvaliacoesService } from '../avaliacoes.service';

@Component({
  selector: 'app-avaliacao-form',
  imports: [ReactiveFormsModule, DatePickerBr],
  templateUrl: './avaliacao-form.html',
  styleUrl: './avaliacao-form.scss'
})
export class AvaliacaoForm {
  private readonly avaliacoesService = inject(AvaliacoesService);
  private readonly fb = inject(FormBuilder);

  @Input({ required: true }) disciplinaId!: string;

  @Output() criada = new EventEmitter<Avaliacao>();

  readonly erro = signal<string | null>(null);

  readonly form = this.fb.group({
    titulo: ['', [Validators.required]],
    tipo: ['PROVA' as TipoAvaliacao, [Validators.required]],
    data: ['', [Validators.required]],
    pontuacao: [10, [Validators.required, Validators.min(1)]]
  });

  cadastrar(): void {
    if (this.form.invalid) {
      return;
    }
    const valores = this.form.getRawValue();
    this.avaliacoesService.create({
      disciplinaId: this.disciplinaId,
      titulo: valores.titulo!,
      tipo: valores.tipo!,
      data: valores.data!,
      pontuacao: valores.pontuacao!
    }).subscribe({
      next: (avaliacao) => {
        this.criada.emit(avaliacao);
        this.form.reset({ titulo: '', tipo: 'PROVA', data: '', pontuacao: 10 });
      },
      error: () => this.erro.set('Não foi possível cadastrar a avaliação.')
    });
  }
}
