export interface GradeBloco {
  id: string;
  disciplinaId: string;
  disciplinaNome: string;
  corIndice: number;
  diaSemana: number;
  horaInicio: number;
  horaFim: number;
}

export interface CreateOuUpdateGradeBlocoPayload {
  disciplinaId: string;
  diaSemana: number;
  horaInicio: number;
  horaFim: number;
}
