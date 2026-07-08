const QUANTIDADE_CORES = 5;

export function tagColorVar(corIndice: number): string {
  return `var(--tag-${corIndice % QUANTIDADE_CORES})`;
}
