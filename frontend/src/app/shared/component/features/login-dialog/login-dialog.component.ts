import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../../core/validator/password-match-validator.validator';
import { BehaviorSubject, catchError, map, Observable, of, Subject, takeUntil, tap } from 'rxjs';
import { AuthenticationRequest } from '../../../../api/model/request/authentication-request';
import { AuthApiService } from '../../../../api/service/rest-api/auth-api.service';
import { MessageService } from 'primeng/api';
import { UserRequest } from '../../../../api/model/request/user-request';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { AuthService } from '../../../../core/service/auth.service';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrl: './login-dialog.component.scss'
})
export class LoginDialogComponent implements OnInit {
  @Input() isDialogVisible = false;
  @Output() isDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  message$ = new BehaviorSubject<string>('');

  isSignInActive = true;
  isSuccess = false;

  isAuthenticated$: Observable<boolean> | undefined;
  isAuthenticatedStatus: boolean = false;

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
    private userApiService: UserApiService,
    private messageService: MessageService,

  ) { }

  ngOnInit(): void {
    this.subscribeToAuthStatus()
  }

  subscribeToAuthStatus() {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(status => {
        this.isAuthenticatedStatus = status;
      });
  }

  onDialogHide(): void {
    this.signInForm.reset();
    this.signUpForm.reset();
    this.isSignInActive = true;
    this.isDialogVisible = false;
    this.isDialogVisibleChange.emit(false);
  }

  onSignIn() {
    if (this.signInForm.valid) {
      const loginRequest: AuthenticationRequest = this.signInForm.value as AuthenticationRequest;
      this.authApiService.login(loginRequest)
        .pipe(
          tap(() => {
            this.isDialogVisible = false;
            this.isDialogVisibleChange.emit(false)
          }),
          map(() => this.handleLoginSuccess()),
          catchError(error => {
            console.error("Error during search:", error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Incorrect User name or Password' });
            return of(null);
          }),
          takeUntil(this.destroy$)
        ).subscribe()
    }
    else {
      this.signInForm.markAllAsTouched();
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill out the form correctly before submitting' });
    }
  }



  onSignUp() {
    if (this.signUpForm.valid) {
      const userRequest: UserRequest = this.signUpForm.value as UserRequest;

      // this.authService.removeAccessToken();

      this.userApiService.create(userRequest).pipe(
        tap(() => this.handleSignUpSuccess()),
        catchError(error => {
          console.error('Error creating user:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to Sign Up' });
          // return throwError(() => new Error(error.message || 'Server error'));
          return of(null);

        }),
        takeUntil(this.destroy$)
      ).subscribe()
    } else {

      this.signUpForm.markAllAsTouched();
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill out the form correctly before submitting' });
    }
  }

  private handleSignUpSuccess(): void {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Sign up successfully, please Sign in again!' });
    // this.authService.setCurrentUserId(this.signInForm.value.userName!);
    // setTimeout(() => {
    //   window.location.reload();
    // }, 2000);
  }


  private handleLoginSuccess() {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Sign in successfully!' });
    this.isDialogVisible = false;
    this.authService.setCurrentUserId(this.signInForm.value.userName!);
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }
}
