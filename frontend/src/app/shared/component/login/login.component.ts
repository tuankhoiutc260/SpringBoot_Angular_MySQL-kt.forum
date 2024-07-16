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
import {  FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../core/validator/password-match-validator.validator';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [MessageService]

})

export class LoginComponent {
  userResponse: UserResponse = {};
  userRequest: UserRequest = {};
  usersResponse: UserResponse[] = [];

  signInForm = new FormGroup({
    userName: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  signUpForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    userName: new FormControl('', [Validators.required, Validators.minLength(6), alphanumericValidator()]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), alphanumericValidator()]),
    rePassword: new FormControl('', [Validators.required, passwordMatchValidator]),
    terms: new FormControl(false, Validators.requiredTrue)
  });

  visible: boolean = false;

  loginRequest: AuthenticationRequest = {
    userName: '',
    password: ''
  };

  message: string = '';

  isSignUpActive: boolean = false;

  toggleActive() {
    this.isSignUpActive = !this.isSignUpActive;
  }

  getSignUpActive(){
    this.authService.isSignUpActive$.subscribe(value => {
      this.isSignUpActive = value;
    });
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private messageService: MessageService,

  ) {
    this.getSignUpActive()
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.message = params['message']
    })
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }


  onSignIn() {
    if (this.signInForm.valid) {
      this.loginRequest = {
        userName: this.signInForm.value.userName!,
        password: this.signInForm.value.password!
      }
      this.authService.removeToken();
      this.authService.removeCurrentUserName();
      this.authService.login(this.loginRequest).subscribe({
        next: (apiResponse: ApiResponse<AuthenticationResponse>) => {
          this.showMessage('info', 'Confirmed', 'Sign in successfully!')
          this.authService.setToken(apiResponse.result?.token!);
          this.authService.setCurrentUserName(this.loginRequest.userName!);
          setTimeout(() => {
            this.router.navigate(['/'])
          }, 2000)
        },
        error: (httpErrorResponse: HttpErrorResponse) => {
          this.showMessage('error', 'Error', httpErrorResponse.error.message)
        }
      })
    }
    else{
      this.signInForm.markAllAsTouched();
      this.showMessage('error', 'Error', 'Please fill out the form correctly before submitting.')
    }
  }

  onSignUp() {
    if (this.signUpForm.valid) {
      this.userRequest = {
        email: this.signUpForm.value.email!,
        userName: this.signUpForm.value.userName!,
        password: this.signUpForm.value.password!
      };
      this.authService.removeToken();
      this.authService.removeCurrentUserName();
      this.userService.create(this.userRequest).subscribe({
        next: () => {
          this.showMessage('info', 'Confirmed', 'Sign up successfully, please Sign in again!')
          this.onLoadPage();
        },
        error: (httpErrorResponse: HttpErrorResponse) => {
          const errorMessage = httpErrorResponse.error.message;
          this.showMessage('error', 'Error', errorMessage)
        }
      });
    } else {
      this.signUpForm.markAllAsTouched();
      this.showMessage('error', 'Error', 'Please fill out the form correctly before submitting.')
    }
  }
}
