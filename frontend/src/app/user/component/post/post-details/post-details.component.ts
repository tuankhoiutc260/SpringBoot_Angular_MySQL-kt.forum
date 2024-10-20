import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BehaviorSubject, combineLatest, Observable, of, Subject, throwError } from 'rxjs';
import { takeUntil, catchError, tap, map } from 'rxjs/operators';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { Location } from '@angular/common';
import { PostLikeApiService } from '../../../../api/service/rest-api/post-like-api.service';
import { PostLikeRequest } from '../../../../api/model/request/post-like-request';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { UserResponse } from '../../../../api/model/response/user-response';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'],
  providers: [MessageService, ConfirmationService],
})
export class PostDetailsComponent implements OnInit, OnDestroy {
  @Input() postResponse!: PostResponse;
  @Input() userLoginId!: string | null;
  @Input() isAuthenticatedStatus!: boolean;

  combinedData$!: Observable<{
    category: CategoryResponse;
    subCategory: SubCategoryResponse;
    user: UserResponse;
  }>;

  private likedSubject = new BehaviorSubject<boolean>(false);
  liked$ = this.likedSubject.asObservable();

  postLikeRequest: PostLikeRequest = {
    postId: ''
  }

  isCreateUpdatePostDialogVisible = false;
  isSignInDialogVisible = false;

  private destroy$ = new Subject<void>();

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
    private userApiService: UserApiService,
    private postApiService: PostApiService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private location: Location,
    private postLikeApiService: PostLikeApiService,
  ) {  }

  ngOnInit() {
    this.initData()
    this.getPostLikedStatus();
  }

  private initData(){
    this.combinedData$ = combineLatest([
      this.categoryApiService.getBySubCategoryId(this.postResponse.subCategoryId),
      this.subCategoryApiService.getById(this.postResponse.subCategoryId),
      this.userApiService.getById(this.postResponse.createdBy)
    ]).pipe(
      map(([category, subCategory, user]) => ({
        category,
        subCategory,
        user
      }))
    );
  }

  onLikePost(postId: string) {
    this.postLikeRequest.postId = postId;

    this.postLikeApiService.toggleLike(this.postLikeRequest).pipe(
      tap(() => {
        this.likedSubject.next(!this.likedSubject.value);
        if (this.postResponse) {
          this.postResponse.likeCount = this.likedSubject.value ? this.postResponse.likeCount + 1 : this.postResponse.likeCount - 1;
        }
      }),
      catchError(error => {
        this.handleError('Error during Like Post', error);
        return of(null);
      }),
      takeUntil(this.destroy$)
    ).subscribe();
  }

  onDeletePost(postId: string) {
    this.confirmationService.confirm({
      message: 'Do you want to Delete this Post?',
      header: 'Delete notification',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",

      accept: () => {
        this.postApiService.deleteById(postId).pipe(
          catchError(error => {
            this.handleError('Failed to Delete Comment', error);
            return throwError(() => new Error(error.message || 'Server error'));
          }),
          takeUntil(this.destroy$)
        ).subscribe(() => {
          this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Post deleted' });
          setTimeout(() => {
            this.location.back();
          }, 1000);
        });
      }
    });
  }

  handleCommentPageChangeClicked() {
    const element = document.getElementById('app-comment');
    const offset = 100;

    if (element) {
      const elementPosition = element.getBoundingClientRect().top + window.pageYOffset - offset;
      window.scrollTo({
        top: elementPosition,
        behavior: 'smooth'
      });
    }
  }

  getPostLikedStatus() {
    if (this.isAuthenticatedStatus) {
      this.postLikeApiService.isLiked({ postId: this.postResponse.id }).pipe(
        catchError(() => of(false)),
        takeUntil(this.destroy$)
      ).subscribe(isLiked => this.likedSubject.next(isLiked));
    }
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
