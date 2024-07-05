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
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../core/validator/password-match-validator.validator';

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

  signUpForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]), // Đã sửa từ emmai thành email
    userName: new FormControl('', [Validators.required, Validators.minLength(6), alphanumericValidator()]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), alphanumericValidator()]),
    rePassword: new FormControl('', [Validators.required, passwordMatchValidator]) // Đảm bảo đã import và sử dụng hàm validator này
  });


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
      if (this.signUpForm.valid) {
      this.userRequest = {
        email: this.signUpForm.value.email!,
        userName: this.signUpForm.value.userName!,
        password: this.signUpForm.value.password!
      };
      this.userService.save(null, this.userRequest).subscribe({
        next: (apiResponse: ApiResponse<UserResponse>) => {
          const userResponse = apiResponse.result;
          if (userResponse) {
            this.usersResponse.unshift(userResponse);
            this.message = 'Sign up successfully, please Sign in!';
            this.loadPage();
          } else {
            console.error('No result found in response:', apiResponse);
          }
        },
        error: (httpErrorResponse: HttpErrorResponse) => {
          const errorMessage = httpErrorResponse.error.message;
          console.error('Sign up error:', errorMessage);
          this.message = errorMessage;
        }
      });
    } else {
      this.message = 'Please fill out the form correctly before submitting.';
    }
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
