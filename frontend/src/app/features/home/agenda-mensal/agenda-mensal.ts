import { Component, computed, input, signal } from '@angular/core';
import { tagColorVar } from '../../../core/theme/tag-color';
import { Avaliacao } from '../../avaliacoes/avaliacao.model';

interface DiaComAvaliacoes {
  data: string;
  dataFormatada: string;
  avaliacoes: Avaliacao[];
}

const NOMES_MES = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

@Component({
  selector: 'app-agenda-mensal',
  imports: [],
  templateUrl: './agenda-mensal.html',
  styleUrl: './agenda-mensal.scss'
})
export class AgendaMensal {
  readonly avaliacoes = input<Avaliacao[]>([]);

  readonly tagColorVar = tagColorVar;

  private readonly hoje = new Date();
  readonly ano = signal(this.hoje.getFullYear());
  readonly mes = signal(this.hoje.getMonth() + 1);

  readonly nomeMesAtual = computed(() => `${NOMES_MES[this.mes() - 1]} de ${this.ano()}`);

  readonly diasComAvaliacao = computed<DiaComAvaliacoes[]>(() => {
    const ano = this.ano();
    const mes = this.mes();
    const mapa = new Map<string, Avaliacao[]>();

    for (const avaliacao of this.avaliacoes()) {
      const [avAno, avMes] = avaliacao.data.split('-').map(Number);
      if (avAno === ano && avMes === mes) {
        const lista = mapa.get(avaliacao.data) ?? [];
        lista.push(avaliacao);
        mapa.set(avaliacao.data, lista);
      }
    }

    return [...mapa.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([data, avaliacoes]) => ({ data, dataFormatada: formatarDiaMes(data), avaliacoes }));
  });

  mesAnterior(): void {
    if (this.mes() === 1) {
      this.mes.set(12);
      this.ano.update((a) => a - 1);
    } else {
      this.mes.update((m) => m - 1);
    }
  }

  proximoMes(): void {
    if (this.mes() === 12) {
      this.mes.set(1);
      this.ano.update((a) => a + 1);
    } else {
      this.mes.update((m) => m + 1);
    }
  }
}

function formatarDiaMes(dataIso: string): string {
  const [, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}`;
}
