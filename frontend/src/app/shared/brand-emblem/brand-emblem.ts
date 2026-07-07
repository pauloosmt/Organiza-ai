import { AfterViewInit, Component, ElementRef, Input, ViewChild } from '@angular/core';

@Component({
  selector: 'app-brand-emblem',
  template: '<canvas #canvas [attr.aria-hidden]="true"></canvas>',
  styles: [':host { display: inline-flex; } canvas { display: block; }']
})
export class BrandEmblem implements AfterViewInit {
  @Input() size = 88;
  @Input() color = '#ffffff';

  @ViewChild('canvas') private canvasRef!: ElementRef<HTMLCanvasElement>;

  ngAfterViewInit(): void {
    this.desenhar();
  }

  private desenhar(): void {
    const canvas = this.canvasRef.nativeElement;
    const dpr = window.devicePixelRatio || 1;
    const size = this.size;
    canvas.width = size * dpr;
    canvas.height = size * dpr;
    canvas.style.width = `${size}px`;
    canvas.style.height = `${size}px`;

    const ctx = canvas.getContext('2d')!;
    ctx.scale(dpr, dpr);
    ctx.fillStyle = this.color;
    ctx.strokeStyle = this.color;

    const cx = size / 2;
    const cy = size / 2;
    const escala = size / 176;

    const chamferedOctagon = (hw: number, hh: number, c: number) => {
      ctx.moveTo(cx - hw + c, cy - hh);
      ctx.lineTo(cx + hw - c, cy - hh);
      ctx.lineTo(cx + hw, cy - hh + c);
      ctx.lineTo(cx + hw, cy + hh - c);
      ctx.lineTo(cx + hw - c, cy + hh);
      ctx.lineTo(cx - hw + c, cy + hh);
      ctx.lineTo(cx - hw, cy + hh - c);
      ctx.lineTo(cx - hw, cy - hh + c);
      ctx.closePath();
    };

    ctx.beginPath();
    chamferedOctagon(28 * escala, 37 * escala, 11 * escala);
    chamferedOctagon(16 * escala, 24 * escala, 4.5 * escala);
    ctx.fill('evenodd');

    const drawStar = (x: number, y: number, outerR: number, innerR: number) => {
      const spikes = 5;
      let rot = -Math.PI / 2;
      const step = Math.PI / spikes;
      ctx.beginPath();
      ctx.moveTo(x, y - outerR);
      for (let i = 0; i < spikes; i++) {
        ctx.lineTo(x + Math.cos(rot) * outerR, y + Math.sin(rot) * outerR);
        rot += step;
        ctx.lineTo(x + Math.cos(rot) * innerR, y + Math.sin(rot) * innerR);
        rot += step;
      }
      ctx.closePath();
      ctx.fill();
    };

    const drawLeaf = (x: number, y: number, pointingAngle: number, length: number, width: number) => {
      ctx.save();
      ctx.translate(x, y);
      ctx.rotate(pointingAngle);
      ctx.beginPath();
      ctx.moveTo(0, 0);
      ctx.quadraticCurveTo(length * 0.45, width, length, 0);
      ctx.quadraticCurveTo(length * 0.45, -width, 0, 0);
      ctx.closePath();
      ctx.fill();
      ctx.restore();
    };

    const drawLaurel = (mirror: boolean) => {
      const dir = mirror ? -1 : 1;
      const stemCx = cx;
      const stemCy = cy + 20 * escala;
      const r = 45 * escala;
      const startDeg = -94;
      const endDeg = 20;
      const leafCount = 9;

      ctx.globalAlpha = 0.85;
      ctx.lineWidth = 1.3 * escala;
      ctx.beginPath();
      for (let i = 0; i <= 40; i++) {
        const t = i / 40;
        const rad = ((startDeg + (endDeg - startDeg) * t) * Math.PI) / 180;
        const x = stemCx + dir * Math.cos(rad) * r;
        const y = stemCy - Math.sin(rad) * r;
        if (i === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
      ctx.globalAlpha = 1;

      for (let i = 0; i < leafCount; i++) {
        const t = i / (leafCount - 1);
        const deg = startDeg + (endDeg - startDeg) * t;
        const rad = (deg * Math.PI) / 180;
        const x = stemCx + dir * Math.cos(rad) * r;
        const y = stemCy - Math.sin(rad) * r;

        const tangent = Math.atan2(-Math.cos(rad), -dir * Math.sin(rad));
        const side = i % 2 === 0 ? 1 : -1;
        const leafAngle = tangent + side * dir * 0.85;

        const len = (10 + t * 6.5) * escala;
        drawLeaf(x, y, leafAngle, len, len * 0.46);
      }

      const startRad = (startDeg * Math.PI) / 180;
      const baseX = stemCx + dir * Math.cos(startRad) * r;
      const baseY = stemCy - Math.sin(startRad) * r;
      drawStar(baseX + dir * 4 * escala, baseY + 8 * escala, 4.2 * escala, 1.8 * escala);
    };

    drawLaurel(false);
    drawLaurel(true);
  }
}
