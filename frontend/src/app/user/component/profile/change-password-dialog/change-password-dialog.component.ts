import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, tap, catchError, takeUntil } from 'rxjs';
import { UserResponse } from '../../../../api/model/response/user-response';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { alphanumericValidator } from '../../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../../core/validator/password-match-validator.validator';

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrl: './change-password-dialog.component.scss'
})
export class ChangePasswordDialogComponent implements OnDestroy {
  @Input() userId!: string | null;
  @Input() userResponse!: UserResponse | null;

  @Input() isChangePasswordDialogActive: boolean = false
  @Output() isChangePasswordDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  changePasswordForm: FormGroup;

  private destroy$ = new Subject<void>();

  constructor(
    private userApiService: UserApiService,
    private fb: FormBuilder,
    private messageService: MessageService,
  ) {
    this.changePasswordForm = this.initChangePasswordForm();
  }

  private initChangePasswordForm(): FormGroup {
    return this.fb.group({
      currentPassword: ['', [Validators.required, Validators.minLength(8), alphanumericValidator()]],
      newPassword: ['', [Validators.required, Validators.minLength(8), alphanumericValidator()]],
      rePassword: ['', [Validators.required, passwordMatchValidator]],
    });
  }

  onChangePassword() {
    if (this.changePasswordForm.valid && this.userId) {
      const changePasswordRequest = {
        currentPassword: this.changePasswordForm.get('currentPassword')?.value,
        newPassword: this.changePasswordForm.get('newPassword')?.value
      };

      this.userApiService.changePassword(this.userId, changePasswordRequest).pipe(
        tap(() => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Password changed successfully'
          });
          setTimeout(() => window.location.reload(), 2000);
        }),
        catchError(error => {
          this.handleError('Error changing password', error);
          throw error;
        }),
        takeUntil(this.destroy$)
      ).subscribe();
    } else {
      this.changePasswordForm.markAllAsTouched();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please fill out the form correctly before submitting'
      });
    }
  }

  onDialogHide(): void {
    this.isChangePasswordDialogActive = false;
    this.isChangePasswordDialogVisibleChange.emit(false);
    this.changePasswordForm.reset();
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}