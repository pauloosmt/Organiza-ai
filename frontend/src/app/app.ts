import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/theme/theme.service';
import { ColdStartBanner } from './shared/cold-start-banner/cold-start-banner';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ColdStartBanner],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly themeService = inject(ThemeService);
}
