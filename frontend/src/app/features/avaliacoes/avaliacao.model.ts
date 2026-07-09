export type TipoAvaliacao = 'PROVA' | 'TRABALHO';

export interface Avaliacao {
  id: string;
  disciplinaId: string;
  disciplinaNome: string;
  corIndice: number;
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number;
  nota: number | null;
}

export interface CreateAvaliacaoPayload {
  disciplinaId: string;
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number;
}

export interface UpdateAvaliacaoPayload {
  titulo: string;
  tipo: TipoAvaliacao;
  data: string;
  pontuacao: number;
  nota: number | null;
}
