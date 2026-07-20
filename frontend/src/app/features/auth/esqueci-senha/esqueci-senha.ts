import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthLayout } from '../../../shared/auth-layout/auth-layout';

@Component({
  selector: 'app-esqueci-senha',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout],
  templateUrl: './esqueci-senha.html',
  styleUrl: '../login/login.scss'
})
export class EsqueciSenha {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly errorMessage = signal<string | null>(null);
  readonly submitting = signal(false);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  submit(): void {
    if (this.form.invalid) {
      return;
    }

    this.errorMessage.set(null);
    this.submitting.set(true);

    const { email } = this.form.getRawValue();

    this.authService.esqueciSenha(email!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/redefinir-senha'], { queryParams: { email } });
      },
      error: () => {
        this.submitting.set(false);
        this.errorMessage.set('Não foi possível enviar o código. Tente novamente.');
      }
    });
  }
}
