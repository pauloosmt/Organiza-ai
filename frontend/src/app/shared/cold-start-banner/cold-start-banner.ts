import { Component, inject } from '@angular/core';
import { ColdStartService } from '../../core/cold-start/cold-start.service';

@Component({
  selector: 'app-cold-start-banner',
  imports: [],
  templateUrl: './cold-start-banner.html',
  styleUrl: './cold-start-banner.scss'
})
export class ColdStartBanner {
  protected readonly coldStart = inject(ColdStartService);
}
