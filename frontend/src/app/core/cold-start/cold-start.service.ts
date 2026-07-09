import { Injectable, signal } from '@angular/core';

const LIMIAR_MS = 2500;

@Injectable({ providedIn: 'root' })
export class ColdStartService {
  readonly aguardando = signal(false);

  private pendentes = 0;
  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  iniciarRequisicao(): void {
    this.pendentes++;
    if (!this.timeoutId && !this.aguardando()) {
      this.timeoutId = setTimeout(() => {
        if (this.pendentes > 0) {
          this.aguardando.set(true);
        }
        this.timeoutId = null;
      }, LIMIAR_MS);
    }
  }

  finalizarRequisicao(): void {
    this.pendentes = Math.max(0, this.pendentes - 1);
    if (this.pendentes === 0) {
      if (this.timeoutId) {
        clearTimeout(this.timeoutId);
        this.timeoutId = null;
      }
      this.aguardando.set(false);
    }
  }
}
