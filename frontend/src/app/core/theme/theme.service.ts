import { Injectable, computed, signal } from '@angular/core';

type Tema = 'light' | 'dark';

const STORAGE_KEY = 'organizaai_theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly temaEscolhido = signal<Tema | null>(this.lerEscolhaSalva());

  readonly tema = computed(() => this.temaEscolhido() ?? this.preferenciaDoSistema());

  constructor() {
    this.aplicar();
  }

  alternar(): void {
    const proximo: Tema = this.tema() === 'dark' ? 'light' : 'dark';
    this.temaEscolhido.set(proximo);
    localStorage.setItem(STORAGE_KEY, proximo);
    this.aplicar();
  }

  private aplicar(): void {
    const tema = this.temaEscolhido();
    if (tema) {
      document.documentElement.setAttribute('data-theme', tema);
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
  }

  private preferenciaDoSistema(): Tema {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  private lerEscolhaSalva(): Tema | null {
    const salvo = localStorage.getItem(STORAGE_KEY);
    return salvo === 'dark' || salvo === 'light' ? salvo : null;
  }
}
