import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BackendWarmupService {
  private readonly http = inject(HttpClient);

  aquecer(): void {
    this.http.get(environment.healthUrl, { responseType: 'text' }).subscribe({ error: () => {} });
  }
}
