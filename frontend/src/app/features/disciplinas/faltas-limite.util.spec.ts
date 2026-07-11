import { atingiuLimiteFaltas, limiteFaltas } from './faltas-limite.util';

describe('faltas-limite.util', () => {
  it('calcula o limite como créditos x 2', () => {
    expect(limiteFaltas({ creditos: 4 })).toBe(8);
  });

  it('não atinge o limite quando faltas < limite', () => {
    expect(atingiuLimiteFaltas({ creditos: 4, faltas: 7 })).toBe(false);
  });

  it('atinge o limite quando faltas === limite', () => {
    expect(atingiuLimiteFaltas({ creditos: 4, faltas: 8 })).toBe(true);
  });

  it('atinge o limite quando faltas > limite', () => {
    expect(atingiuLimiteFaltas({ creditos: 4, faltas: 9 })).toBe(true);
  });

  it('com créditos 0, o limite é 0 e qualquer falta (inclusive zero) já atinge', () => {
    expect(atingiuLimiteFaltas({ creditos: 0, faltas: 1 })).toBe(true);
    expect(atingiuLimiteFaltas({ creditos: 0, faltas: 0 })).toBe(true);
  });
});
