import { Component, inject } from '@angular/core';
import { OnlineStatusService } from '../../../core/connectivity/online-status.service';

@Component({
  selector: 'app-offline-banner',
  imports: [],
  templateUrl: './offline-banner.html',
  styleUrl: './offline-banner.scss'
})
export class OfflineBanner {
  readonly onlineStatus = inject(OnlineStatusService);
}
