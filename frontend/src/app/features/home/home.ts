import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { PeriodoContextService } from '../../core/periodo/periodo-context.service';
import { tagColorVar } from '../../core/theme/tag-color';
import { Disciplina } from '../disciplinas/disciplina.model';
import { DisciplinasService } from '../disciplinas/disciplinas.service';
import { GradeBloco } from '../grade/grade-bloco.model';
import { GradeService } from '../grade/grade.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly disciplinasService = inject(DisciplinasService);
  private readonly gradeService = inject(GradeService);
  private readonly periodoContext = inject(PeriodoContextService);

  readonly currentUser = this.authService.currentUser;
  readonly periodoAtual = this.periodoContext.periodoAtual;

  private readonly disciplinas = signal<Disciplina[]>([]);
  private readonly blocos = signal<GradeBloco[]>([]);

  readonly tagColorVar = tagColorVar;

  readonly resumo = computed(() => {
    const disciplinas = this.disciplinas();
    return {
      totalDisciplinas: disciplinas.length,
      totalCreditos: disciplinas.reduce((soma, d) => soma + d.creditos, 0),
      disciplinaMaisFaltas: disciplinas.reduce<Disciplina | null>(
        (maior, atual) => (maior === null || atual.faltas > maior.faltas ? atual : maior),
        null
      )
    };
  });

  readonly agendaHoje = computed(() => {
    const diaHoje = new Date().getDay();
    return this.blocos()
      .filter((b) => b.diaSemana === diaHoje)
      .sort((a, b) => a.horaInicio - b.horaInicio);
  });

  constructor() {
    effect(() => {
      const periodo = this.periodoContext.periodoAtual();
      if (periodo) {
        this.disciplinasService.list(periodo.id).subscribe((disciplinas) => this.disciplinas.set(disciplinas));
        this.gradeService.list(periodo.id).subscribe((blocos) => this.blocos.set(blocos));
      } else {
        this.disciplinas.set([]);
        this.blocos.set([]);
      }
    });
  }

  ngOnInit(): void {
    this.periodoContext.carregar();
  }
}
