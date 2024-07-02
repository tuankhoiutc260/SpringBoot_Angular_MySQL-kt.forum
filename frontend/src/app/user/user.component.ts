import { Component } from '@angular/core';
import { AuthService } from '../core/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  currentUserName: string = '';

  constructor(
    private authService: AuthService,
    private router: Router

  ) { }

  ngOnInit() {
    this.getCurrentUserName();
  }

  getCurrentUserName() {
    this.currentUserName = this.authService.getCurrentUserName();
  }

  logout() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();
    this.router.navigate(['/login']);
  }

}
