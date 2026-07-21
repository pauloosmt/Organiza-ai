import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { tagColorVar } from '../../../core/theme/tag-color';
import { formatarDataBr } from '../../../core/utils/data-br';
import { Avaliacao } from '../avaliacao.model';
import { AvaliacoesService } from '../avaliacoes.service';

@Component({
  selector: 'li[app-avaliacao-linha]',
  imports: [],
  templateUrl: './avaliacao-linha.html',
  styleUrl: './avaliacao-linha.scss'
})
export class AvaliacaoLinha {
  private readonly avaliacoesService = inject(AvaliacoesService);

  @Input({ required: true }) avaliacao!: Avaliacao;
  @Input() mostrarDisciplina = false;

  @Output() atualizada = new EventEmitter<Avaliacao>();
  @Output() removida = new EventEmitter<Avaliacao>();

  readonly erro = signal<string | null>(null);

  readonly tagColorVar = tagColorVar;
  readonly formatarDataBr = formatarDataBr;

  atualizarNota(notaTexto: string): void {
    const nota = notaTexto.trim() === '' ? null : Number(notaTexto);
    if (nota !== null && Number.isNaN(nota)) {
      return;
    }
    if (nota !== null && nota > this.avaliacao.pontuacao) {
      this.erro.set(`A nota máxima permitida é ${this.avaliacao.pontuacao}.`);
      return;
    }
    this.erro.set(null);
    this.avaliacoesService.update(this.avaliacao.id, {
      titulo: this.avaliacao.titulo,
      tipo: this.avaliacao.tipo,
      data: this.avaliacao.data,
      pontuacao: this.avaliacao.pontuacao,
      nota
    }).subscribe({
      next: (atualizada) => this.atualizada.emit(atualizada),
      error: () => this.erro.set('Não foi possível salvar a nota.')
    });
  }

  remover(): void {
    this.avaliacoesService.remove(this.avaliacao.id).subscribe(() => this.removida.emit(this.avaliacao));
  }
}
