import { Component } from '@angular/core';
import { AuthService } from '../../../core/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  login: any = { userName: '', password: '' }

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onLogin() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();

    this.authService.login(this.login).subscribe({
      next: (response) => {
        if (response.code === 1000 && response.result.authenticated) {
          this.authService.setToken(response.result.token);
          this.authService.setCurrentUserName(this.login.userName); // Lưu tên người dùng hiện tại

          this.router.navigate(["/"]);
          console.log(response);
        } else {
          console.log('Authentication failed');
        }
      },
      error: (error) => {
        console.log("Login error: ", error);
      }
    });
  }

}
