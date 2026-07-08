import { Component, ElementRef, HostListener, OnInit, ViewChild, computed, effect, inject, signal } from '@angular/core';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';
import { tagColorVar } from '../../core/theme/tag-color';
import { Disciplina } from '../disciplinas/disciplina.model';
import { DisciplinasService } from '../disciplinas/disciplinas.service';
import { GradeBloco } from './grade-bloco.model';
import { GradeService } from './grade.service';

interface Dia {
  value: number;
  label: string;
}

interface CelulaVazia {
  dia: number;
  hora: number;
}

interface PopupState {
  dia: number;
  horaInicio: number;
  horaFim: number;
  blocoId: string | null;
  query: string;
}

@Component({
  selector: 'app-grade-board',
  imports: [],
  templateUrl: './grade-board.html',
  styleUrl: './grade-board.scss'
})
export class GradeBoard implements OnInit {
  private readonly gradeService = inject(GradeService);
  private readonly disciplinasService = inject(DisciplinasService);
  readonly periodoContext = inject(PeriodoContextService);

  @ViewChild('board') boardRef?: ElementRef<HTMLElement>;

  readonly dias: Dia[] = [
    { value: 1, label: 'Segunda' },
    { value: 2, label: 'Terça' },
    { value: 3, label: 'Quarta' },
    { value: 4, label: 'Quinta' },
    { value: 5, label: 'Sexta' },
    { value: 6, label: 'Sábado' }
  ];

  readonly horas = Array.from({ length: 17 }, (_, i) => i + 6);

  readonly blocos = signal<GradeBloco[]>([]);
  readonly disciplinas = signal<Disciplina[]>([]);
  readonly popup = signal<PopupState | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly exportando = signal(false);

  readonly tagColorVar = tagColorVar;

  readonly legenda = computed(() => {
    const vistos = new Set<string>();
    const itens: { disciplinaId: string; disciplinaNome: string; corIndice: number }[] = [];
    for (const bloco of this.blocos()) {
      if (!vistos.has(bloco.disciplinaId)) {
        vistos.add(bloco.disciplinaId);
        itens.push({ disciplinaId: bloco.disciplinaId, disciplinaNome: bloco.disciplinaNome, corIndice: bloco.corIndice });
      }
    }
    return itens;
  });

  private dragging = false;
  private dragDia: number | null = null;
  private dragStart: number | null = null;
  private dragEnd: number | null = null;
  readonly dragTick = signal(0);

  readonly celulasVazias = computed<CelulaVazia[]>(() => {
    const ocupadas = new Set<string>();
    for (const bloco of this.blocos()) {
      for (let h = bloco.horaInicio; h < bloco.horaFim; h++) {
        ocupadas.add(`${bloco.diaSemana}-${h}`);
      }
    }
    const celulas: CelulaVazia[] = [];
    for (const dia of this.dias) {
      for (const hora of this.horas) {
        if (!ocupadas.has(`${dia.value}-${hora}`)) {
          celulas.push({ dia: dia.value, hora });
        }
      }
    }
    return celulas;
  });

  readonly sugestoes = computed<Disciplina[]>(() => {
    const p = this.popup();
    if (!p) {
      return [];
    }
    const termo = p.query.trim().toLowerCase();
    if (!termo) {
      return this.disciplinas();
    }
    return this.disciplinas().filter((d) => d.nome.toLowerCase().includes(termo));
  });

  constructor() {
    effect(() => {
      const periodo = this.periodoContext.periodoAtual();
      if (periodo) {
        this.carregar(periodo.id);
      } else {
        this.blocos.set([]);
        this.disciplinas.set([]);
      }
    });
  }

  ngOnInit(): void {
    this.periodoContext.carregar();
  }

  carregar(periodoId: string): void {
    this.gradeService.list(periodoId).subscribe((blocos) => this.blocos.set(blocos));
    this.disciplinasService.list(periodoId).subscribe((disciplinas) => this.disciplinas.set(disciplinas));
  }

  onMouseDown(dia: number, hora: number): void {
    this.dragging = true;
    this.dragDia = dia;
    this.dragStart = hora;
    this.dragEnd = hora;
    this.dragTick.update((v) => v + 1);
  }

  onMouseEnter(dia: number, hora: number): void {
    if (this.dragging && dia === this.dragDia) {
      this.dragEnd = hora;
      this.dragTick.update((v) => v + 1);
    }
  }

  @HostListener('document:mouseup')
  onDocumentMouseUp(): void {
    if (this.dragging && this.dragDia !== null && this.dragStart !== null && this.dragEnd !== null) {
      const horaInicio = Math.min(this.dragStart, this.dragEnd);
      const horaFim = Math.max(this.dragStart, this.dragEnd) + 1;
      this.abrirPopup(this.dragDia, horaInicio, horaFim, null, '');
    }
    this.dragging = false;
    this.dragDia = null;
    this.dragStart = null;
    this.dragEnd = null;
    this.dragTick.update((v) => v + 1);
  }

  isSelecting(dia: number, hora: number): boolean {
    this.dragTick();
    if (!this.dragging || dia !== this.dragDia || this.dragStart === null || this.dragEnd === null) {
      return false;
    }
    const min = Math.min(this.dragStart, this.dragEnd);
    const max = Math.max(this.dragStart, this.dragEnd);
    return hora >= min && hora <= max;
  }

  onClickBloco(bloco: GradeBloco): void {
    this.abrirPopup(bloco.diaSemana, bloco.horaInicio, bloco.horaFim, bloco.id, bloco.disciplinaNome);
  }

  abrirPopup(dia: number, horaInicio: number, horaFim: number, blocoId: string | null, query: string): void {
    this.errorMessage.set(null);
    this.popup.set({ dia, horaInicio, horaFim, blocoId, query });
  }

  fecharPopup(): void {
    this.popup.set(null);
  }

  atualizarQuery(valor: string): void {
    const p = this.popup();
    if (p) {
      this.popup.set({ ...p, query: valor });
    }
  }

  selecionarDisciplina(disciplina: Disciplina): void {
    this.salvarBloco(disciplina.id);
  }

  cadastrarECriarBloco(): void {
    const p = this.popup();
    const periodo = this.periodoContext.periodoAtual();
    if (!p || !p.query.trim() || !periodo) {
      return;
    }
    this.disciplinasService.create(p.query.trim(), periodo.id).subscribe({
      next: (disciplina) => {
        this.disciplinas.update((atual) => [...atual, disciplina]);
        this.salvarBloco(disciplina.id);
      },
      error: () => this.errorMessage.set('Não foi possível cadastrar a disciplina.')
    });
  }

  private salvarBloco(disciplinaId: string): void {
    const p = this.popup();
    if (!p) {
      return;
    }
    const payload = {
      disciplinaId,
      diaSemana: p.dia,
      horaInicio: p.horaInicio,
      horaFim: p.horaFim
    };

    const request$ = p.blocoId ? this.gradeService.update(p.blocoId, payload) : this.gradeService.create(payload);

    request$.subscribe({
      next: () => {
        this.recarregarPeriodoAtual();
        this.fecharPopup();
      },
      error: (err) => {
        this.errorMessage.set(
          err.status === 409 ? 'Já existe uma disciplina alocada nesse horário.' : 'Não foi possível salvar o bloco.'
        );
      }
    });
  }

  removerBloco(): void {
    const p = this.popup();
    if (!p || !p.blocoId) {
      return;
    }
    this.gradeService.remove(p.blocoId).subscribe(() => {
      this.recarregarPeriodoAtual();
      this.fecharPopup();
    });
  }

  private recarregarPeriodoAtual(): void {
    const periodo = this.periodoContext.periodoAtual();
    if (periodo) {
      this.carregar(periodo.id);
    }
  }

  async exportarPng(): Promise<void> {
    const arquivo = await this.gerarCanvas();
    if (!arquivo) {
      return;
    }
    const link = document.createElement('a');
    link.download = 'grade-horario.png';
    link.href = arquivo.toDataURL('image/png');
    link.click();
  }

  async exportarPdf(): Promise<void> {
    const canvas = await this.gerarCanvas();
    if (!canvas) {
      return;
    }
    const { jsPDF } = await import('jspdf');
    const orientation = canvas.width > canvas.height ? 'l' : 'p';
    const pdf = new jsPDF(orientation, 'px', [canvas.width, canvas.height]);
    pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, canvas.width, canvas.height);
    pdf.save('grade-horario.pdf');
  }

  private async gerarCanvas(): Promise<HTMLCanvasElement | null> {
    if (!this.boardRef) {
      return null;
    }
    this.exportando.set(true);
    try {
      const html2canvas = (await import('html2canvas')).default;
      return await html2canvas(this.boardRef.nativeElement, { backgroundColor: '#ffffff' });
    } finally {
      this.exportando.set(false);
    }
  }
}
