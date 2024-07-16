import { Component, ElementRef, ViewChild } from '@angular/core';
import { AuthService } from '../core/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  currentUserName: string = '';
  // @ViewChild('header', { static: false }) header!: ElementRef;
  @ViewChild('header', { static: false }) header!: ElementRef;


  constructor(
    private authService: AuthService,
    private router: Router

  ) { }

  ngOnInit() {
    this.getCurrentUserName();

  }

  ngAfterViewInit() {
    this.adjustContentMargin();
  }

  getCurrentUserName() {
    this.currentUserName = this.authService.getCurrentUserName();
  }

  logout() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();
    this.router.navigate(['/login']);
  }
  adjustContentMargin() {
    if (this.header && this.header.nativeElement) {
      const headerHeight = this.header.nativeElement.offsetHeight;
      const content = document.querySelector('.content') as HTMLElement;
      if (content) {
        content.style.marginTop = `${headerHeight}px`;
      }
    }
  }
}
