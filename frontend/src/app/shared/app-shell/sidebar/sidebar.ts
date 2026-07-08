import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { BrandEmblem } from '../../brand-emblem/brand-emblem';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, BrandEmblem],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {}
