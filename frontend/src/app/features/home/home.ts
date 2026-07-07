import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { NavBar } from '../../shared/nav-bar/nav-bar';

@Component({
  selector: 'app-home',
  imports: [NavBar],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  private readonly authService = inject(AuthService);

  readonly currentUser = this.authService.currentUser;
}
