import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthLayout } from '../../../shared/auth-layout/auth-layout';
import { PasswordInput } from '../../../shared/password-input/password-input';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout, PasswordInput],
  templateUrl: './register.html',
  styleUrl: '../login/login.scss'
})
export class Register {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly errorMessage = signal<string | null>(null);
  readonly submitting = signal(false);

  readonly form = this.fb.group({
    name: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmarSenha: ['', [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid) {
      return;
    }

    const { name, email, password, confirmarSenha } = this.form.getRawValue();
    if (password !== confirmarSenha) {
      this.errorMessage.set('As senhas não coincidem.');
      return;
    }

    this.errorMessage.set(null);
    this.submitting.set(true);

    this.authService.register(name!, email!, password!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/verificar-email'], { queryParams: { email } });
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(
          err.status === 409 ? 'Já existe uma conta com esse e-mail.' : 'Não foi possível concluir o cadastro.'
        );
      }
    });
  }
}
