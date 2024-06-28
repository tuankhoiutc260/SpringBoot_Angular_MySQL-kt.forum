import { Component } from '@angular/core';
import { AuthService } from '../core/service/auth.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  currentUserName: string = '';

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.getCurrentUserName();
  }

  getCurrentUserName() {
    this.currentUserName = this.authService.getCurrentUserName();
  }

}
