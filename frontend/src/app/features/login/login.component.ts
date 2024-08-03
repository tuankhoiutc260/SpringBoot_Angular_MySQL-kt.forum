import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../core/validator/password-match-validator.validator';
import { AuthApiService } from '../../api/service/rest-api/auth-api.service';
import { UserApiService } from '../../api/service/rest-api/user-api.service';
import { AuthService } from '../../core/service/auth.service';
import { Subscription } from 'rxjs';
import { AuthenticationRequest } from '../../api/model/request/authentication-request';
import { UserRequest } from '../../api/model/request/user-request';
import { ApiResponse } from '../../api/model/response/api-response';
import { AuthenticationResponse } from '../../api/model/response/authenticated-response';
import { UserResponse } from '../../api/model/response/user-response';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {
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
  
  private subscription: Subscription = new Subscription();

  toggleActive() {
    this.isSignUpActive = !this.isSignUpActive;
  }

  getSignUpActive(){
    this.authService.isSignUpActive$.subscribe(value => {
      this.isSignUpActive = value;
    });
  }

  constructor(
    private authApiService: AuthApiService,
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userApiService: UserApiService,
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

  userLoginInfo: UserResponse | null = null;

  onSignIn() {
    if (this.signInForm.valid) {
      this.loginRequest = {
        userName: this.signInForm.value.userName!,
        password: this.signInForm.value.password!
      }
      this.authService.removeToken();
      this.authService.removeCurrentUserName();
      const sub = this.authApiService.login(this.loginRequest).subscribe({
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
      });
      this.subscription.add(sub);
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
      const sub = this.userApiService.create(this.userRequest).subscribe({
        next: () => {
          this.showMessage('info', 'Confirmed', 'Sign up successfully, please Sign in again!')
          this.onLoadPage();
        },
        error: (httpErrorResponse: HttpErrorResponse) => {
          const errorMessage = httpErrorResponse.error.message;
          this.showMessage('error', 'Error', errorMessage)
        }
      });
      this.subscription.add(sub);
    } else {
      this.signUpForm.markAllAsTouched();
      this.showMessage('error', 'Error', 'Please fill out the form correctly before submitting.')
    }
  }


 
  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}