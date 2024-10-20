import { Component, OnInit, OnDestroy, Renderer2, ElementRef } from '@angular/core';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
  providers: [MessageService]
})
export class UserComponent implements OnInit, OnDestroy {
  private scrollListener: (() => void) | null = null;
  private header: HTMLElement | null = null;

  constructor(
    private renderer: Renderer2,
    private el: ElementRef,
  ) {}

  ngOnInit() {
    this.header = document.querySelector('header');
    if (this.header) {
      this.scrollListener = this.onScroll.bind(this);
      window.addEventListener('scroll', this.scrollListener);
    } else {
      console.error('Header element not found');
    }
  }

  ngOnDestroy() {
    if (this.scrollListener) {
      window.removeEventListener('scroll', this.scrollListener);
    }
  }

  private onScroll() {
    if (!this.header) return;

    const scrollPosition = window.scrollY;
    const maxScroll = 200;
    const opacity = Math.min(scrollPosition / maxScroll, 0.5);
    
    this.renderer.setStyle(
      this.header,
      'background-color',
      `rgba(0, 0, 0, ${opacity})`
    );
  }
}