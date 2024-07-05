import { Component, Renderer2, ElementRef } from '@angular/core';
import { AuthService } from '../../../core/service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { AuthenticationRequest } from '../../../core/interface/request/authentication-request';
import { AuthenticationResponse } from '../../../core/interface/response/authenticated-response';
import { HttpErrorResponse } from '@angular/common/http';
import { UserResponse } from '../../../core/interface/response/user-response';
import { UserRequest } from '../../../core/interface/request/user-request';
import { UserService } from '../../../core/service/user.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'], // Sửa thành styleUrls,
  providers: [MessageService]

})

export class LoginComponent {
  userResponse: UserResponse = {};
  userRequest: UserRequest = {};
  usersResponse: UserResponse[] = [];


  visible: boolean = false;
  // logoUrl = '../assets/images/kt-blog-logo.png';


  login: AuthenticationRequest = {
    userName: '',
    password: ''
  };

  message: string = '';

  isActive: boolean = false;

  toggleActive() {
    this.isActive = !this.isActive;
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private messageService: MessageService,


  ) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.message = params['message']
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
          this.message = "Login error: No token found in response";
        }
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        const errorMessage = httpErrorResponse.error.message;
        console.error("Login error: ", errorMessage);
        this.message = errorMessage;
      }
    });
  }
  // showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
  //   this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  // }
  loadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }
  signUp() {
    // this.userRequest = {
    //   ...this.userResponse,
    //   role: this.userResponse.role?.id
    // };
    console.log("Sign up");

    this.userService.save(null, this.userRequest).subscribe({
      next: (apiResponse: ApiResponse<UserResponse>) => {
        const userResponse = apiResponse.result;
        if (userResponse) {
          this.usersResponse.unshift(userResponse);
          // this.showMessage('info', 'Confirmed', 'User created');

          // this.isVisible = false;
          // this.resetForm();
          // this.loadPage();
          this.message = 'Sign up successfully, please Sign in!'
          this.loadPage()
        } else {
          console.error('No result found in response:', apiResponse);
        }
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        const errorMessage = httpErrorResponse.error.message;
        console.error("Login error: ", errorMessage);
        this.message = errorMessage;
      }
    });
  }


  isValidEmail(): boolean {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const email = this.userRequest.email;
  
    if (!email) {
      this.message = 'Email can not be null FE'
      return false;
    }
  
    if (!emailRegex.test(email)) {
      this.message = 'Email should be valid FE'
      return false;
    }
  
    this.message = ''
    return true;
  }

  validateEmail(): void {
    this.isValidEmail();
  }
  

}
