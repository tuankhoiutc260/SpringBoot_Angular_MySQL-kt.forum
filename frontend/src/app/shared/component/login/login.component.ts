import { Component } from '@angular/core';
import { AuthService } from '../../../core/service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { AuthenticationRequest } from '../../../core/interface/request/authentication-request';
import { AuthenticationResponse } from '../../../core/interface/response/authenticated-response';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'] // Sửa thành styleUrls
})
export class LoginComponent {
  login: AuthenticationRequest = {
    userName: '',
    password: ''
  };
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.errorMessage = params['message']
    })
  }

  onLogin() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();

    this.authService.login(this.login).subscribe({
      next: (apiResponse: ApiResponse<AuthenticationResponse>) => {
        if (apiResponse.result) {
          this.authService.setToken(apiResponse.result.token!);
          this.authService.setCurrentUserName(this.login.userName!);

          this.router.navigate(['/']);
          console.log(apiResponse);
        } else {
          this.errorMessage = "Login error: No token found in response";
        }
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        const errorMessage = httpErrorResponse.error.message;
        console.error("Login error: ", errorMessage);
        this.errorMessage = errorMessage;
      }
    });
  }
}
