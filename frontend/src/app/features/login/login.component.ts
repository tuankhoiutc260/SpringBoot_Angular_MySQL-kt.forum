import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../core/validator/password-match-validator.validator';
import { AuthApiService } from '../../api/service/rest-api/auth-api.service';
import { UserApiService } from '../../api/service/rest-api/user-api.service';
import { AuthService } from '../../core/service/auth.service';
import { BehaviorSubject, catchError, map, Subject, takeUntil, tap, throwError } from 'rxjs';
import { AuthenticationRequest } from '../../api/model/request/authentication-request';
import { UserRequest } from '../../api/model/request/user-request';
import { AuthenticationResponse } from '../../api/model/response/authenticated-response';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {
  message$ = new BehaviorSubject<string>('');
  isSignUpActive = false;

  isSignUpActive$ = new BehaviorSubject<boolean>(false);
  private destroy$ = new Subject<void>();

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

  toggleActive() {
    this.isSignUpActive = !this.isSignUpActive;

    this.signInForm.markAsUntouched();
    this.signInForm.updateValueAndValidity();
  }

  getSignUpActive() {
    this.authService.isSignUpActive$.subscribe(value => {
      this.isSignUpActive = value;
    });
  }

  ngOnInit(): void {
    this.subscribeToQueryParams();
    this.subscribeToSignUpStatus();
  }

  private subscribeToQueryParams(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.message$.next(params['message'] || '');
    });
  }

  private subscribeToSignUpStatus(): void {
    this.authService.isSignUpActive$.subscribe(value => {
      this.isSignUpActive$.next(value);
    });
  }

  onSignIn() {
    if (this.signInForm.valid) {
      const loginRequest: AuthenticationRequest = this.signInForm.value as AuthenticationRequest;

      this.authService.removeToken();
      this.authService.removeCurrentUserName();

      this.authApiService.login(loginRequest)
        .pipe(
          map(authenticationResponse => this.handleLoginSuccess(authenticationResponse)),
          catchError(error => {
            console.error('Error fetching Post:', error);
            this.handleError('Failed to Login')
            return throwError(() => new Error(error.message || 'Server error'));
          }),
          takeUntil(this.destroy$)
        ).subscribe()
    }
    else {
      this.signInForm.markAllAsTouched();
      this.handleError('Please fill out the form correctly before submitting')
    }
  }

  onSignUp() {
    if (this.signUpForm.valid) {
      const userRequest: UserRequest = this.signUpForm.value as UserRequest;

      this.authService.removeToken();
      this.authService.removeCurrentUserName();

      this.userApiService.create(userRequest).pipe(
        tap(() => this.handleSignUpSuccess()),
        catchError(error => {
          console.error('Error creating user:', error);
          this.handleError('Failed to Sign Up');
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      ).subscribe()
    } else {

      this.signUpForm.markAllAsTouched();
      this.handleError('Please fill out the form correctly before submitting')
    }
  }

  private handleError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage });
  }

  private handleSuccess(successMessage: string): void {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: successMessage });
  }

  private handleLoginSuccess(authenticationResponse: AuthenticationResponse) {
    this.handleSuccess('Sign in successfully!')
    this.authService.setToken(authenticationResponse.token!);
    this.authService.setCurrentUserName(this.signInForm.value.userName!);
    setTimeout(() => {
      this.router.navigate(['/']);
    }, 2000);
  }

  private handleSignUpSuccess(): void {
    this.handleSuccess('Sign up successfully, please Sign in again!')
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
