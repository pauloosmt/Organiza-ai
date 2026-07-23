import { Component, Input, forwardRef, signal } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-password-input',
  imports: [],
  templateUrl: './password-input.html',
  styleUrl: './password-input.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PasswordInput),
      multi: true
    }
  ]
})
export class PasswordInput implements ControlValueAccessor {
  @Input() inputId = '';
  @Input() autocomplete = 'current-password';
  @Input() placeholder = '';

  readonly visivel = signal(false);
  readonly valor = signal('');
  readonly desabilitado = signal(false);

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  writeValue(value: string): void {
    this.valor.set(value ?? '');
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.desabilitado.set(isDisabled);
  }

  aoDigitar(valor: string): void {
    this.valor.set(valor);
    this.onChange(valor);
  }

  aoPerderFoco(): void {
    this.onTouched();
  }

  alternarVisibilidade(): void {
    this.visivel.update((v) => !v);
  }
}
