import { Component, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ThemeService } from '../../core/theme/theme.service';
import { formatarDataBr } from '../../core/utils/data-br';
import { PasswordInput } from '../../shared/password-input/password-input';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule, PasswordInput],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss'
})
export class Perfil {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  readonly themeService = inject(ThemeService);

  readonly currentUser = this.authService.currentUser;

  membroDesde(createdAt: string): string {
    return formatarDataBr(createdAt.split('T')[0]);
  }

  // Nome
  readonly nomeForm = this.fb.group({
    name: ['', [Validators.required]]
  });
  readonly nomeSalvo = signal(false);
  readonly nomeErro = signal<string | null>(null);

  constructor() {
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.nomeForm.patchValue({ name: user.name }, { emitEvent: false });
      }
    });
  }

  salvarNome(): void {
    if (this.nomeForm.invalid) {
      return;
    }
    const { name } = this.nomeForm.getRawValue();
    this.nomeErro.set(null);
    this.nomeSalvo.set(false);
    this.authService.atualizarNome(name!).subscribe({
      next: () => this.nomeSalvo.set(true),
      error: () => this.nomeErro.set('Não foi possível salvar o nome.')
    });
  }

  // Troca de senha
  readonly faseSenha = signal<'idle' | 'codigo'>('idle');
  readonly senhaForm = this.fb.group({
    novaSenha: ['', [Validators.required, Validators.minLength(8)]],
    confirmarSenha: ['', [Validators.required]]
  });
  readonly codigoSenhaForm = this.fb.group({
    codigo: ['', [Validators.required]]
  });
  readonly senhaErro = signal<string | null>(null);
  readonly senhaInfo = signal<string | null>(null);
  readonly senhaEnviando = signal(false);
  readonly reenviandoCodigoSenha = signal(false);

  iniciarTrocaSenha(): void {
    if (this.senhaForm.invalid) {
      return;
    }
    const { novaSenha, confirmarSenha } = this.senhaForm.getRawValue();
    if (novaSenha !== confirmarSenha) {
      this.senhaErro.set('As senhas não coincidem.');
      return;
    }
    this.senhaErro.set(null);
    this.senhaInfo.set(null);
    this.senhaEnviando.set(true);
    this.authService.iniciarTrocaSenha(novaSenha!).subscribe({
      next: () => {
        this.senhaEnviando.set(false);
        this.faseSenha.set('codigo');
      },
      error: () => {
        this.senhaEnviando.set(false);
        this.senhaErro.set('Não foi possível iniciar a troca de senha.');
      }
    });
  }

  confirmarTrocaSenha(): void {
    if (this.codigoSenhaForm.invalid) {
      return;
    }
    const { codigo } = this.codigoSenhaForm.getRawValue();
    this.senhaErro.set(null);
    this.senhaEnviando.set(true);
    this.authService.confirmarTrocaSenha(codigo!).subscribe({
      next: () => {
        this.senhaEnviando.set(false);
        this.faseSenha.set('idle');
        this.senhaForm.reset();
        this.codigoSenhaForm.reset();
        this.senhaInfo.set('Senha alterada com sucesso.');
      },
      error: () => {
        this.senhaEnviando.set(false);
        this.senhaErro.set('Código inválido ou expirado.');
      }
    });
  }

  reenviarCodigoTrocaSenha(): void {
    if (this.reenviandoCodigoSenha()) {
      return;
    }
    this.reenviandoCodigoSenha.set(true);
    this.senhaErro.set(null);
    this.authService.reenviarCodigoTrocaSenha().subscribe({
      next: () => {
        this.reenviandoCodigoSenha.set(false);
        this.senhaInfo.set('Novo código enviado.');
      },
      error: () => {
        this.reenviandoCodigoSenha.set(false);
        this.senhaErro.set('Não foi possível reenviar o código.');
      }
    });
  }

  cancelarTrocaSenha(): void {
    this.faseSenha.set('idle');
    this.senhaForm.reset();
    this.codigoSenhaForm.reset();
    this.senhaErro.set(null);
    this.senhaInfo.set(null);
  }

  // Excluir conta
  readonly excluirPopupAberto = signal(false);
  readonly excluirForm = this.fb.group({
    senha: ['', [Validators.required]]
  });
  readonly excluirErro = signal<string | null>(null);
  readonly excluindo = signal(false);

  abrirExcluirPopup(): void {
    this.excluirPopupAberto.set(true);
    this.excluirErro.set(null);
    this.excluirForm.reset();
  }

  fecharExcluirPopup(): void {
    this.excluirPopupAberto.set(false);
  }

  confirmarExclusao(): void {
    if (this.excluirForm.invalid) {
      return;
    }
    const { senha } = this.excluirForm.getRawValue();
    this.excluindo.set(true);
    this.excluirErro.set(null);
    this.authService.excluirConta(senha!).subscribe({
      next: () => {
        this.excluindo.set(false);
        this.router.navigateByUrl('/login');
      },
      error: () => {
        this.excluindo.set(false);
        this.excluirErro.set('Senha incorreta.');
      }
    });
  }
}
