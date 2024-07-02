import { Component } from '@angular/core';
import { AuthService } from '../core/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-staff',
  templateUrl: './staff.component.html',
  styleUrl: './staff.component.scss'
})
export class StaffComponent {
  menus = [
    {
      name: 'Posts',
      link: ''
    },
    {
      name: 'User Management',
      link: 'user-management'
    }
  ]
  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  logout() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();
    this.router.navigate(['/login']);
  }


}
