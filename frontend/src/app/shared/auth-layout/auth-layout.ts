import { Component } from '@angular/core';
import { BrandEmblem } from '../brand-emblem/brand-emblem';

@Component({
  selector: 'app-auth-layout',
  imports: [BrandEmblem],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.scss'
})
export class AuthLayout {}
