export type TipoAvaliacao = 'PROVA' | 'TRABALHO' | 'RECUPERACAO' | 'ATIVIDADE';

export interface Avaliacao {
  id: string;
  disciplinaId: string;
  disciplinaNome: string;
  corIndice: number;
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number | null;
  nota: number | null;
}

export interface CreateAvaliacaoPayload {
  disciplinaId: string;
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number | null;
}

export interface UpdateAvaliacaoPayload {
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number | null;
  nota: number | null;
}
