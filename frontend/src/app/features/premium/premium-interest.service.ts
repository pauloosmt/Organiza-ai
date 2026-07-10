import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PremiumInterestStatus } from './premium-interest.model';

@Injectable({ providedIn: 'root' })
export class PremiumInterestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/premium/interesse`;

  status(): Observable<PremiumInterestStatus> {
    return this.http.get<PremiumInterestStatus>(this.baseUrl);
  }

  register(): Observable<PremiumInterestStatus> {
    return this.http.post<PremiumInterestStatus>(this.baseUrl, {});
  }
}
