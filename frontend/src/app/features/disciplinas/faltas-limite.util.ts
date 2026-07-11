import { Disciplina } from './disciplina.model';

export function limiteFaltas(disciplina: Pick<Disciplina, 'creditos'>): number {
  return disciplina.creditos * 2;
}

export function atingiuLimiteFaltas(disciplina: Pick<Disciplina, 'creditos' | 'faltas'>): boolean {
  return disciplina.faltas >= limiteFaltas(disciplina);
}
