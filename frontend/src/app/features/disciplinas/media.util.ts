import { Avaliacao } from '../avaliacoes/avaliacao.model';
import { Periodo } from '../../core/periodo/periodo.model';

export type EstadoMedia = 'APROVADO' | 'RECUPERACAO' | 'REPROVADO';

export interface ResultadoMedia {
  mediaAtual: number;
  estado: EstadoMedia;
  notaRecuperacao: number | null;
  mediaFinal: number | null;
}

export function calcularMedia(
  periodo: Pick<Periodo, 'mediaMinimaPassar' | 'mediaMinimaRecuperacao'>,
  avaliacoesDaDisciplina: Avaliacao[]
): ResultadoMedia {
  const normais = avaliacoesDaDisciplina.filter((a) => a.tipo !== 'RECUPERACAO');
  const mediaAtual = normais.reduce((soma, a) => soma + (a.nota ?? 0), 0);

  const recuperacao = avaliacoesDaDisciplina.find((a) => a.tipo === 'RECUPERACAO' && a.nota !== null);
  const notaRecuperacao = recuperacao?.nota ?? null;
  const mediaFinal = notaRecuperacao !== null ? (mediaAtual + notaRecuperacao) / 2 : null;

  // Uma vez lançada a nota de recuperação, o resultado é definitivo (só
  // aprovado ou reprovado) — não faz sentido continuar mostrando "precisa
  // de recuperação" depois que ela já foi feita.
  const estado: EstadoMedia =
    mediaFinal !== null
      ? mediaFinal >= periodo.mediaMinimaPassar
        ? 'APROVADO'
        : 'REPROVADO'
      : mediaAtual >= periodo.mediaMinimaPassar
        ? 'APROVADO'
        : mediaAtual >= periodo.mediaMinimaRecuperacao
          ? 'RECUPERACAO'
          : 'REPROVADO';

  return { mediaAtual, estado, notaRecuperacao, mediaFinal };
}
