import { Component, OnDestroy, OnInit } from '@angular/core';
import { catchError, map, Observable, of, Subject, Subscription, switchMap, takeUntil, throwError } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { AuthService } from '../../../../core/service/auth.service';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { PostResponse } from '../../../../api/model/response/post-response';
import { UserResponse } from '../../../../api/model/response/user-response';
import { AuthApiService } from '../../../../api/service/rest-api/auth-api.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../../core/validator/password-match-validator.validator';
import { UserRequest } from '../../../../api/model/request/user-request';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class ProfileComponent implements OnInit, OnDestroy {
  userId: string | undefined;
  userName: string | undefined;
  isEditUserInfo = false;

  userResponse$: Observable<UserResponse | null> | undefined;

  private destroy$ = new Subject<void>();

  userForm = new FormGroup({
    userName: new FormControl(''),
    email: new FormControl(''),
    fullName: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), alphanumericValidator()]),
    rePassword: new FormControl('', [Validators.required, passwordMatchValidator]),
  });

  constructor(
    private postApiService: PostApiService,
    private userApiService: UserApiService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,

  ) { }

  ngOnInit() {
    this.activatedRoute.params.pipe(
      map(params => params['userName']),
      takeUntil(this.destroy$)
    ).subscribe(userName => {
      this.userName = userName;
    });

    this.initializeUserInfo()
  }

  //   getUserLoginInfo() {
  //   this.authService.fetchAndSetUserLoginInfo();
  //     this.authService.getUserLoginInfo().subscribe(userLoginInfo => {
  //       this.userLoginInfo = userLoginInfo;
  //       if (this.userName === this.userLoginInfo?.userName) {
  //         this.canEdit = true;
  //       }
  //     })

  // }

  initializeUserInfo() {
    if (this.userName) {
      this.userResponse$ = this.userApiService.getByUserName(this.userName)
        .pipe(
          catchError(error => {
            console.error('Error during update:', error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error fetching User Information!' });
            return throwError(() => new Error(error.message || 'Server error'));
          })
        );

      this.userResponse$
        .pipe(takeUntil(this.destroy$))
        .subscribe(userResponse => {
          if (userResponse) {
            this.userId = userResponse.id;
            this.userForm.patchValue({
              userName: userResponse.userName,
              email: userResponse.email,
              fullName: userResponse.fullName,
            });
          }
        });
    }
  }

  onToggleDialogUpdateUser() {
    this.isEditUserInfo = !this.isEditUserInfo;
    this.userForm.markAsUntouched();
    this.userForm.updateValueAndValidity();
  }


  onUpdateUser() {
    if (this.userForm.valid) {
      console.log(this.userForm.value)
      this.isEditUserInfo = false;
      const userRequest: UserRequest = this.userForm.value as UserRequest;
      this.userApiService.update(this.userId!, userRequest)
        .pipe(
          catchError(error => {
            console.error('Error fetching Post:', error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating User Information!' });
            return throwError(() => new Error(error.message || 'Server error'));
          }),
          takeUntil(this.destroy$)
        ).subscribe();
    } else {
      this.userForm.markAllAsTouched();
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill out the form correctly before submitting' });
    }
  }



  // totalPosts: number = 0;
  // postsResponse: PostResponse[] = [];
  // userResponse!: UserResponse
  // canEdit!: boolean
  // private subscription: Subscription = new Subscription();



  // ngOnInit(): void {
  //   this.activatedRoute.params.subscribe(params => {
  //     this.userName = params['userName'];
  //   });

  //   this.getUserInfo()
  //   this.getUserLoginInfo()
  // }

  // defaultUserLoginInfo!: UserResponse;
  // userLoginInfo: UserResponse | null = null;



  // activeButton: string = 'bars';
  // toggleActive(button: string) {
  //   this.activeButton = button;
  // }

  // getUserInfo() {
  //   const sub = this.userApiService.getByUserName(this.userName).subscribe({
  //     next: (apiResponse) => {
  //       const userInfo = apiResponse;
  //       if (userInfo) {
  //         this.userResponse = userInfo;
  //         // this.getPostsCreatedBy();
  //       } else {
  //         console.error('No result found in response:', apiResponse);
  //       }
  //     },
  //     error: (error) => {
  //       console.error('Error fetching user information:', error);
  //     }
  //   });
  //   this.subscription.add(sub);
  // }

  // totalLikes: number = 0
  // // getPostsCreatedBy(): void {
  // //   const sub = this.postApiService.findByCreatedBy(this.userResponse.id!).subscribe({
  // //     next: (apiResponse: ApiResponse<PostResponse[]>) => {
  // //       const postResponseList = apiResponse.result;
  // //       if (postResponseList) {
  // //         this.postsResponse = postResponseList;
  // //         this.totalPosts = postResponseList.length;
  // //         this.totalLikes = postResponseList.reduce((sum, post) => sum + (post.countLikes || 0), 0);

  // //         if (this.userName === this.userLoginInfo?.userName) {
  // //           this.canEdit = true
  // //         }
  // //       } else {
  // //         console.error('No result found in response:', apiResponse.message);
  // //       }
  // //     },
  // //     error: (error) => {
  // //       console.error('Error fetching posts created by: ' + this.userName, error);
  // //     }
  // //   });
  // //   this.subscription.add(sub);
  // // }

  // // getPostsLiked(): void {
  // //   const sub = this.postApiService.findPostsLiked(this.userResponse.id!).subscribe({
  // //     next: (apiResponse: ApiResponse<PostResponse[]>) => {
  // //       const postResponseList = apiResponse.result;
  // //       if (postResponseList) {
  // //         this.postsResponse = postResponseList;
  // //         this.canEdit = false
  // //       } else {
  // //         console.error('No result found in response:', apiResponse.message);
  // //       }
  // //     },
  // //     error: (error) => {
  // //       console.error('Error fetching posts liked:', error);
  // //     }
  // //   });
  // //   this.subscription.add(sub);
  // // }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
