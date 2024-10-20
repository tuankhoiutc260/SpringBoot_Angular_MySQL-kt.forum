import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SafeResourceUrl } from '@angular/platform-browser';
import { MessageService } from 'primeng/api';
import { Subject, tap, catchError, takeUntil, Observable } from 'rxjs';
import { UserResponse } from '../../../../api/model/response/user-response';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { UpdateProfileRequest } from '../../../../api/model/request/change-pasword-request copy';
import { AuthService } from '../../../../core/service/auth.service';

@Component({
  selector: 'app-edit-profile-dialog',
  templateUrl: './edit-profile-dialog.component.html',
  styleUrl: './edit-profile-dialog.component.scss'
})
export class EditProfileDialogComponent implements OnInit {
  @Input() userId!: string | null;
  @Input() userResponse!: UserResponse | null;

  @Input() isEditProfileDialogActive: boolean = false
  @Output() isEditProfileDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  userLoginId$: Observable<string | null>;

  editUserProfileForm: FormGroup;

  imagePreview: SafeResourceUrl | null = null;
  fileName: string = '';

  private destroy$ = new Subject<void>();

  constructor(
    private userApiService: UserApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private messageService: MessageService,
  ) {
    this.editUserProfileForm = this.initEditUserProfileForm();
    this.userLoginId$ = this.authService.currentUserId$;
  }

  ngOnInit(): void {
    this.userLoginId$.pipe(
      takeUntil(this.destroy$)
    ).subscribe((userLoginId: string | null) => {
      this.updateFormControlsState(this.userId === userLoginId);
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['userResponse'] && changes['userResponse'].currentValue) {
      this.populateForm();
    }
  }

  private initEditUserProfileForm(): FormGroup {
    return this.fb.group({
      imageFile: [null],
      fullName: [{ value: '', disabled: true }, [Validators.required]],
      aboutMe: [{ value: '', disabled: true }]
    });
  }

  private updateFormControlsState(isCurrentUser: boolean): void {
    if (isCurrentUser) {
      this.editUserProfileForm.get('fullName')?.enable();
      this.editUserProfileForm.get('aboutMe')?.enable();
    } else {
      this.editUserProfileForm.get('fullName')?.disable();
      this.editUserProfileForm.get('aboutMe')?.disable();
    }
  }

  populateForm() {
    if (this.userResponse) {
      this.editUserProfileForm.patchValue({
        fullName: this.userResponse.fullName,
        aboutMe: this.userResponse.aboutMe,
      });
    }
  }

  onSelectImageFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.type.match(/image\/*/) == null) {
        this.resetImageSelection();
        return;
      }
      this.readAndPreviewImage(file);
    }
  }

  private readAndPreviewImage(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.imagePreview = reader.result as string;
      this.fileName = file.name;
      this.editUserProfileForm.patchValue({ imageFile: file });
    };
    reader.readAsDataURL(file);
  }

  onRemoveImageFile(): void {
    this.resetImageSelection();
    this.editUserProfileForm.patchValue({ imageFile: null });
    const inputElement = document.getElementById('input') as HTMLInputElement;
    if (inputElement) {
      inputElement.value = '';
    }
  }

  private resetImageSelection(): void {
    this.imagePreview = null;
    this.fileName = '';
  }

  onUpdateUser(): void {
    if (this.editUserProfileForm.valid) {
      const editUserProfileRequest = {
        fullName: this.editUserProfileForm.get('fullName')?.value,
        imageFile: this.editUserProfileForm.get('imageFile')?.value,
        aboutMe: this.editUserProfileForm.get('aboutMe')?.value
      };

      console.log(editUserProfileRequest);

      this.userApiService.updateProfile(this.userId!, editUserProfileRequest as UpdateProfileRequest).pipe(
        tap(() => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'User updated successfully'
          });
          setTimeout(() => window.location.reload(), 2000);
        }),
        catchError(error => {
          this.handleError('Error updating User information', error);
          throw error;
        }),
        takeUntil(this.destroy$)
      ).subscribe();
    } else {
      this.editUserProfileForm.markAllAsTouched();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please fill out the form correctly before submitting'
      });
    }
  }

  onDialogHide(): void {
    this.isEditProfileDialogActive = false;
    this.isEditProfileDialogVisibleChange.emit(false);
    this.editUserProfileForm.reset();
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