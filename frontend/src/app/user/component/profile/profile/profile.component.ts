import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MessageService } from 'primeng/api';
import { UserResponse } from '../../../../api/model/response/user-response';
import { BehaviorSubject, Observable, Subject, combineLatest, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap, takeUntil } from 'rxjs/operators';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { PostResponse } from '../../../../api/model/response/post-response';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { AuthService } from '../../../../core/service/auth.service';
import { OverlayPanel } from 'primeng/overlaypanel';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  providers: [MessageService]
})
export class ProfileComponent implements OnInit, OnDestroy {
  @ViewChild('op') op!: OverlayPanel;

  @Input() set userId(value: string | null) {
    this._userId = value;
    this.userIdSubject.next(value);
  }

  get userId(): string | null {
    return this._userId;
  }

  private _userId: string | null = null;
  private userIdSubject = new BehaviorSubject<string | null>(null);

  @Input() userResponse!: UserResponse | null;
  userLoginId$: Observable<string | null>;

  readonly POSTS_PAGE_SIZE = 12;
  postsLikedCurrentPage$ = new BehaviorSubject<number>(0);
  postsCreatedByCurrentPage$ = new BehaviorSubject<number>(0);

  isPostLikeActive$ = new BehaviorSubject<boolean>(false);
  postLikedResponsePage$!: Observable<PagedResponse<PostResponse[]>>;
  postCreatedByResponsePage$!: Observable<PagedResponse<PostResponse[]>>;

  isChangePasswordDialogActive = false;
  isEditProfileDialogActive = false;

  activeTab: 'posts' | 'liked' = 'posts';

  currentPostResponsePage$!: Observable<PagedResponse<PostResponse[]>>;

  private destroy$ = new Subject<void>();

  constructor(
    private postApiService: PostApiService,
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
    this.userLoginId$ = this.authService.currentUserId$;
  }

  ngOnInit() {
    this.initializeUserId();
    this.initializePosts();
  }

  private initializeUserId() {
    this.activatedRoute.params.pipe(
      map(params => params['userId']),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(userId => {
      this.userId = userId;
    });
  }

  handleClick(event: MouseEvent) {
    this.userLoginId$.pipe(takeUntil(this.destroy$)).subscribe(loginId => {
      if (this.userId === loginId) {
        this.op.toggle(event);
      } else {
        this.isEditProfileDialogActive = true;
      }
    });
  }

  getTotalViews(posts: PostResponse[]): number {
    return posts.reduce((total, post) => total + (post.viewCount ?? 0), 0);
  }

  getTotalLikes(posts: PostResponse[]): number {
    return posts.reduce((total, post) => total + (post.likeCount ?? 0), 0);
  }

  onActivePostLiked() {
    this.activeTab = 'liked';
    this.isPostLikeActive$.next(true);
    this.updateUrlAndFetchPosts(0);
  }

  onActivePostCreatedBy() {
    this.activeTab = 'posts';
    this.isPostLikeActive$.next(false);
    this.updateUrlAndFetchPosts(0);
  }

  private initializePosts() {
    this.loadPostsCreatedByUser();
    this.loadPostsLikedByUser();
    this.setupCurrentPostResponsePage();
  }

  private loadPostsCreatedByUser() {
    this.postCreatedByResponsePage$ = combineLatest([
      this.userIdSubject.pipe(distinctUntilChanged()),
      this.postsCreatedByCurrentPage$
    ]).pipe(
      takeUntil(this.destroy$),
      switchMap(([userId, page]) => {
        if (!userId) return this.getEmptyPagedResponse();
        return this.initPostResponsePageByUserId(userId, page);
      })
    );
  }

  private loadPostsLikedByUser() {
    this.postLikedResponsePage$ = combineLatest([
      this.userIdSubject.pipe(distinctUntilChanged()),
      this.postsLikedCurrentPage$
    ]).pipe(
      takeUntil(this.destroy$),
      switchMap(([userId, page]) => {
        if (!userId) return this.getEmptyPagedResponse();
        return this.initPostResponsePageLiked(userId, page);
      })
    );
  }

  private setupCurrentPostResponsePage() {
    this.currentPostResponsePage$ = combineLatest([
      this.isPostLikeActive$,
      this.postLikedResponsePage$,
      this.postCreatedByResponsePage$
    ]).pipe(
      map(([isPostLikeActive, likedPosts, createdPosts]) =>
        isPostLikeActive ? likedPosts : createdPosts
      )
    );
  }

  private initPostResponsePageByUserId(userId: string, page: number): Observable<PagedResponse<PostResponse[]>> {
    return this.postApiService.getPostsByCreatedBy(userId, page, this.POSTS_PAGE_SIZE).pipe(
      catchError(error => this.handleError('Error fetching Posts', error))
    );
  }

  private initPostResponsePageLiked(userId: string, page: number): Observable<PagedResponse<PostResponse[]>> {
    return this.postApiService.getPostsLiked(userId, page, this.POSTS_PAGE_SIZE).pipe(
      catchError(error => this.handleError('Error fetching Liked Posts', error))
    );
  }

  private getEmptyPagedResponse(): Observable<PagedResponse<PostResponse[]>> {
    return of({ content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } });
  }

  @Output() pageChangeClicked = new EventEmitter<void>();

  onPageChange(event: any) {
    const page = event.page;
    if (this.isPostLikeActive$.value) {
      this.postsLikedCurrentPage$.next(page);
    } else {
      this.postsCreatedByCurrentPage$.next(page);
    }
    this.pageChangeClicked.emit();
    this.updateUrlAndFetchPosts(page);
  }

  private updateUrlAndFetchPosts(page: number) {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page },
      queryParamsHandling: 'merge'
    });
  }

  private handleError(message: string, error: any): Observable<PagedResponse<PostResponse[]>> {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
    return this.getEmptyPagedResponse();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
