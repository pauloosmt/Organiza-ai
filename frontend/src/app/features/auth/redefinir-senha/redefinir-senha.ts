import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthLayout } from '../../../shared/auth-layout/auth-layout';

@Component({
  selector: 'app-redefinir-senha',
  imports: [ReactiveFormsModule, AuthLayout],
  templateUrl: './redefinir-senha.html',
  styleUrl: '../login/login.scss'
})
export class RedefinirSenha {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private readonly email = this.route.snapshot.queryParamMap.get('email') ?? '';

  readonly errorMessage = signal<string | null>(null);
  readonly infoMessage = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly reenviando = signal(false);

  readonly form = this.fb.group({
    codigo: ['', [Validators.required]],
    novaSenha: ['', [Validators.required, Validators.minLength(8)]],
    confirmarSenha: ['', [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid || !this.email) {
      return;
    }

    const { codigo, novaSenha, confirmarSenha } = this.form.getRawValue();
    if (novaSenha !== confirmarSenha) {
      this.errorMessage.set('As senhas não coincidem.');
      return;
    }

    this.errorMessage.set(null);
    this.infoMessage.set(null);
    this.submitting.set(true);

    this.authService.redefinirSenha(this.email, codigo!, novaSenha!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/login'], { queryParams: { senhaRedefinida: '1' } });
      },
      error: () => {
        this.submitting.set(false);
        this.errorMessage.set('Código inválido ou expirado.');
      }
    });
  }

  reenviarCodigo(): void {
    if (!this.email || this.reenviando()) {
      return;
    }

    this.errorMessage.set(null);
    this.infoMessage.set(null);
    this.reenviando.set(true);

    this.authService.esqueciSenha(this.email).subscribe({
      next: () => {
        this.reenviando.set(false);
        this.infoMessage.set('Novo código enviado.');
      },
      error: () => {
        this.reenviando.set(false);
        this.errorMessage.set('Não foi possível reenviar o código.');
      }
    });
  }
}
