import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class OnlineStatusService {
  private readonly onlineSignal = signal(navigator.onLine);
  readonly online = this.onlineSignal.asReadonly();

  constructor() {
    window.addEventListener('online', () => this.onlineSignal.set(true));
    window.addEventListener('offline', () => this.onlineSignal.set(false));
  }
}
