import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../../core/validator/password-match-validator.validator';
import { catchError, throwError, takeUntil, Subject, Observable, map } from 'rxjs';
import { UserRequest } from '../../../../api/model/request/user-request';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { SafeResourceUrl } from '@angular/platform-browser';
import { UserResponse } from '../../../../api/model/response/user-response';

@Component({
  selector: 'app-create-update-user',
  templateUrl: './create-update-user.component.html',
  styleUrl: './create-update-user.component.scss'
})
export class CreateUpdateUserComponent implements OnInit, OnDestroy {
  @Input() isEditUserInfo = false;
  @Input() userResponse: UserResponse | undefined
  @Output() isDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  userDetailsForm = new FormGroup({
    imageFile: new FormControl<File | null>(null),
    userName: new FormControl(''),
    email: new FormControl(''),
    fullName: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), alphanumericValidator()]),
    rePassword: new FormControl('', [Validators.required, passwordMatchValidator]),
  });
  userId: string | undefined;
  userName: string | undefined;

  // userResponse$!: Observable<UserResponse | null>;
  isActiveImage: boolean = false;
  isImageValid: boolean = true
  imagePreview: SafeResourceUrl | null = null;
  fileName: string = '';
  private destroy$ = new Subject<void>();

  constructor(
    private userApiService: UserApiService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,

  ) { }

  ngOnInit(): void {
    // this.activatedRoute.params.pipe(
    //   map(params => params['userName']),
    //   takeUntil(this.destroy$)
    // ).subscribe(userName => {
    //   this.userName = userName;
    //   this.initializeUserInfo(userName)

    // });

    if (this.userResponse) {
      this.populateForm();
    }
  }

  populateForm(): void {
    if (this.userResponse) {
      // this.userApiService.getById(this.userResponse.id).pipe(
      //   takeUntil(this.destroy$)
      // ).subscribe({
      //   next: (user) => {
      //     // this.selectedCategoryId = category.id;
      //     // this.initializeSubCategories(this.selectedCategoryId, 0, 100);
      //   },
      //   error: (error) => {
      //     console.error('Error loading category:', error);
      //     this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error loading category' });
      //   }
      // });

      this.userDetailsForm.patchValue({
        // title: this.userResponse.title,
        userName: this.userResponse.userName,
        email: this.userResponse.email,
        fullName: this.userResponse.fullName,
        // tags: this.userResponse.tags
      });
    }
  }

  onDialogHide(): void {
    this.isDialogVisibleChange.emit(false);
  }
  onUpdateUser() {
    // if (this.userDetailsForm.valid) {
    //   // this.isEditUserInfo = false;

    //   console.log(this.userDetailsForm.value)
  
    //   // Sử dụng getRawValue để lấy tất cả giá trị bao gồm cả các trường bị disable
    //   const userRequest: UserRequest = this.userDetailsForm.getRawValue() as UserRequest;
  
    //   // In ra giá trị đúng từ getRawValue
    //   console.log('Form values (getRawValue):', userRequest);
  
    //   this.userApiService.update(this.userId!, userRequest)
    //     .pipe(
    //       catchError(error => {
    //         console.error('Error updating User:', error);
    //         this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating User Information!' });
    //         return throwError(() => new Error(error.message || 'Server error'));
    //       }),
    //       takeUntil(this.destroy$)
    //     ).subscribe();
    // } else {
    //   this.userDetailsForm.markAllAsTouched();
    //   this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill out the form correctly before submitting' });
    // }
  }

  onSelectFile(event: any): void {
    const selectedFiles = event.target.files;
  
    if (selectedFiles.length > 0) {
      const file: File = selectedFiles[0];
      const mimeType = file.type;
  
      if (mimeType.match(/image\/*/) == null) {
        this.isImageValid = false;
        this.isActiveImage = false;
        this.imagePreview = null; // Cập nhật giá trị thành null thay vì chuỗi rỗng
        this.fileName = '';
        return;
      } else {
        this.isImageValid = true;
      }
  
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = reader.result;
        this.isActiveImage = true;
        this.fileName = file.name;
      };
  
      // Cập nhật giá trị cho trường imageFile trong userDetailsForm
      this.userDetailsForm.patchValue({
        imageFile: file
      });
      console.log(this.userDetailsForm.value)
  
      reader.readAsDataURL(file);
    }
  }

  // initializeUserInfo(userName: string) {
  //   if (userName) {
  //     this.userResponse$ = this.userApiService.getByUserName(userName)
  //       .pipe(
  //         catchError(error => {
  //           console.error('Error during update:', error);
  //           this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error fetching User Information!' });
  //           return throwError(() => new Error(error.message || 'Server error'));
  //         }),
  //         takeUntil(this.destroy$)
  //       );
  
  //     // Subscribe to the observable to get the userResponse value and update the form
  //     this.userResponse$.subscribe(userResponse => {
  //       if (userResponse) {
  //         this.userId = userResponse.id
  //         // Update form values
  //         this.userDetailsForm.patchValue({
  //           userName: userResponse.userName,
  //           email: userResponse.email,
  //           fullName: userResponse.fullName
  //         });

  //         console.log(this.userDetailsForm.value)
  
  //         // Disable the form controls
  //         this.userDetailsForm.get('userName')?.disable();
  //         this.userDetailsForm.get('email')?.disable();
  //       } else {
  //         console.log('No user response found.');
  //       }
  //     });
  //   } else {
  //     console.error('User name is not defined.');
  //   }
  // }
  
  

  onToggleDialogUpdateUser() {
    // this.isEditUserInfo = !this.isEditUserInfo;
    this.userDetailsForm.markAsUntouched();
    this.userDetailsForm.updateValueAndValidity();
  }


  

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
