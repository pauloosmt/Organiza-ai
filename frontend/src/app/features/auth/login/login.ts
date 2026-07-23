import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthLayout } from '../../../shared/auth-layout/auth-layout';
import { PasswordInput } from '../../../shared/password-input/password-input';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout, PasswordInput],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly errorMessage = signal<string | null>(null);
  readonly emailNaoVerificado = signal(false);
  readonly submitting = signal(false);
  readonly infoMessage = signal<string | null>(this.mensagemInicial());

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid) {
      return;
    }

    this.errorMessage.set(null);
    this.emailNaoVerificado.set(false);
    this.infoMessage.set(null);
    this.submitting.set(true);

    const { email, password } = this.form.getRawValue();

    this.authService.login(email!, password!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.submitting.set(false);
        if (err.status === 403) {
          this.emailNaoVerificado.set(true);
          this.errorMessage.set('Verifique seu email antes de entrar.');
        } else {
          this.errorMessage.set('E-mail ou senha inválidos.');
        }
      }
    });
  }

  get emailDigitado(): string {
    return this.form.getRawValue().email ?? '';
  }

  private mensagemInicial(): string | null {
    const params = this.route.snapshot.queryParamMap;
    if (params.get('verificado')) {
      return 'Email verificado! Faça login.';
    }
    if (params.get('senhaRedefinida')) {
      return 'Senha redefinida! Faça login com a nova senha.';
    }
    return null;
  }
}
