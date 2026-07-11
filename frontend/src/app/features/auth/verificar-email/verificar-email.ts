import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthLayout } from '../../../shared/auth-layout/auth-layout';

@Component({
  selector: 'app-verificar-email',
  imports: [ReactiveFormsModule, AuthLayout],
  templateUrl: './verificar-email.html',
  styleUrl: '../login/login.scss'
})
export class VerificarEmail {
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
    codigo: ['', [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid || !this.email) {
      return;
    }

    this.errorMessage.set(null);
    this.infoMessage.set(null);
    this.submitting.set(true);

    const { codigo } = this.form.getRawValue();

    this.authService.verificarEmail(this.email, codigo!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/login'], { queryParams: { verificado: '1' } });
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

    this.authService.reenviarCodigo(this.email).subscribe({
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
