import { Component, EventEmitter, Input, OnInit, Output, inject, signal } from '@angular/core';
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
export class AvaliacaoForm implements OnInit {
  private readonly avaliacoesService = inject(AvaliacoesService);
  private readonly fb = inject(FormBuilder);

  @Input() disciplinaId = '';
  @Input() avaliacao: Avaliacao | null = null;

  @Output() criada = new EventEmitter<Avaliacao>();
  @Output() atualizada = new EventEmitter<Avaliacao>();
  @Output() cancelada = new EventEmitter<void>();

  readonly erro = signal<string | null>(null);

  readonly form = this.fb.group({
    titulo: ['', [Validators.required]],
    tipo: ['PROVA' as TipoAvaliacao, [Validators.required]],
    data: ['', [Validators.required]],
    pontuacao: [10, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    if (this.avaliacao) {
      this.form.patchValue({
        titulo: this.avaliacao.titulo,
        tipo: this.avaliacao.tipo,
        data: this.avaliacao.data,
        pontuacao: this.avaliacao.pontuacao
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    if (this.avaliacao) {
      this.salvarEdicao(this.avaliacao);
    } else {
      this.cadastrar();
    }
  }

  cancelar(): void {
    this.cancelada.emit();
  }

  private cadastrar(): void {
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

  private salvarEdicao(avaliacaoAtual: Avaliacao): void {
    const valores = this.form.getRawValue();
    if (avaliacaoAtual.nota !== null && valores.pontuacao! < avaliacaoAtual.nota) {
      this.erro.set(`A pontuação não pode ser menor que a nota já lançada (${avaliacaoAtual.nota}).`);
      return;
    }
    this.erro.set(null);
    this.avaliacoesService.update(avaliacaoAtual.id, {
      titulo: valores.titulo!,
      tipo: valores.tipo!,
      data: valores.data!,
      pontuacao: valores.pontuacao!,
      nota: avaliacaoAtual.nota
    }).subscribe({
      next: (atualizada) => this.atualizada.emit(atualizada),
      error: () => this.erro.set('Não foi possível salvar a edição.')
    });
  }
}
