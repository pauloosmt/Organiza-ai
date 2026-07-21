import { Component, ElementRef, HostListener, computed, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { formatarDataBr } from '../../core/utils/data-br';

interface DiaCalendario {
  dia: number;
  noMesAtual: boolean;
  iso: string;
}

const NOMES_MES = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

const NOMES_DIA_SEMANA = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'];

const DROPDOWN_LARGURA = 260;
const DROPDOWN_ALTURA_ESTIMADA = 320;
const MARGEM_VIEWPORT = 8;

@Component({
  selector: 'app-date-picker-br',
  imports: [],
  templateUrl: './date-picker-br.html',
  styleUrl: './date-picker-br.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerBr),
      multi: true
    }
  ]
})
export class DatePickerBr implements ControlValueAccessor {
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  readonly aberto = signal(false);
  readonly valorIso = signal<string | null>(null);
  readonly desabilitado = signal(false);
  readonly dropdownPosition = signal<{ top: number; left: number } | null>(null);

  private readonly hoje = new Date();
  readonly anoExibido = signal(this.hoje.getFullYear());
  readonly mesExibido = signal(this.hoje.getMonth() + 1);

  readonly nomesDiaSemana = NOMES_DIA_SEMANA;

  readonly textoExibido = computed(() => {
    const iso = this.valorIso();
    return iso ? formatarDataBr(iso) : '';
  });

  readonly nomeMesExibido = computed(() => `${NOMES_MES[this.mesExibido() - 1]} de ${this.anoExibido()}`);

  readonly dias = computed<DiaCalendario[]>(() => {
    const ano = this.anoExibido();
    const mes = this.mesExibido();
    const primeiroDiaDoMes = new Date(ano, mes - 1, 1);
    const cursor = new Date(primeiroDiaDoMes);
    cursor.setDate(cursor.getDate() - primeiroDiaDoMes.getDay());

    const dias: DiaCalendario[] = [];
    for (let i = 0; i < 42; i++) {
      dias.push({
        dia: cursor.getDate(),
        noMesAtual: cursor.getMonth() === mes - 1,
        iso: formatarIso(cursor)
      });
      cursor.setDate(cursor.getDate() + 1);
    }
    return dias;
  });

  private onChange: (value: string | null) => void = () => {};
  private onTouched: () => void = () => {};

  @HostListener('document:click', ['$event'])
  aoClicarFora(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.aberto.set(false);
    }
  }

  writeValue(value: string | null): void {
    this.valorIso.set(value);
    if (value) {
      const [ano, mes] = value.split('-').map(Number);
      this.anoExibido.set(ano);
      this.mesExibido.set(mes);
    }
  }

  registerOnChange(fn: (value: string | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.desabilitado.set(isDisabled);
  }

  alternar(): void {
    if (this.desabilitado()) {
      return;
    }
    this.aberto.update((a) => !a);
    if (this.aberto()) {
      const rect = this.elementRef.nativeElement.querySelector('.campo')!.getBoundingClientRect();

      let left = rect.left;
      left = Math.min(left, window.innerWidth - DROPDOWN_LARGURA - MARGEM_VIEWPORT);
      left = Math.max(left, MARGEM_VIEWPORT);

      let top = rect.bottom + 6;
      if (top + DROPDOWN_ALTURA_ESTIMADA > window.innerHeight) {
        top = Math.max(rect.top - DROPDOWN_ALTURA_ESTIMADA - 6, MARGEM_VIEWPORT);
      }

      this.dropdownPosition.set({ top, left });
      this.onTouched();
    }
  }

  mesAnterior(): void {
    if (this.mesExibido() === 1) {
      this.mesExibido.set(12);
      this.anoExibido.update((a) => a - 1);
    } else {
      this.mesExibido.update((m) => m - 1);
    }
  }

  proximoMes(): void {
    if (this.mesExibido() === 12) {
      this.mesExibido.set(1);
      this.anoExibido.update((a) => a + 1);
    } else {
      this.mesExibido.update((m) => m + 1);
    }
  }

  selecionar(dia: DiaCalendario): void {
    this.valorIso.set(dia.iso);
    this.onChange(dia.iso);
    this.aberto.set(false);
  }
}

function formatarIso(data: Date): string {
  const ano = data.getFullYear();
  const mes = (data.getMonth() + 1).toString().padStart(2, '0');
  const dia = data.getDate().toString().padStart(2, '0');
  return `${ano}-${mes}-${dia}`;
}
