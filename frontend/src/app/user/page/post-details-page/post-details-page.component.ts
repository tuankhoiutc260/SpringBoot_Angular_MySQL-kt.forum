import { Component, OnDestroy, OnInit } from '@angular/core';
import { SafeHtml, DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, Subject, map, shareReplay, takeUntil, switchMap, tap, catchError, of } from 'rxjs';
import { CommentResponse } from '../../../api/model/response/comment-response';
import { PostResponse } from '../../../api/model/response/post-response';
import { PostApiService } from '../../../api/service/rest-api/post-api.service';
import { AuthService } from '../../../core/service/auth.service';

@Component({
  selector: 'app-post-details-page',
  templateUrl: './post-details-page.component.html',
  styleUrl: './post-details-page.component.scss',
  providers: [MessageService],
})
export class PostDetailsPageComponent implements OnInit, OnDestroy {
  postId$!: Observable<string>;
  postResponse$!: Observable<PostResponse | null>;
  safeContent$!: Observable<SafeHtml>;

  isAuthenticated$: Observable<boolean>;
  isAuthenticatedStatus: boolean = false;

  userLoginId$: Observable<string | null>;

  private destroy$ = new Subject<void>();

  constructor(
    private postApiService: PostApiService,
    private activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private messageService: MessageService,
    private authService: AuthService,
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.userLoginId$ = this.authService.currentUserId$;
  }

  ngOnInit() {
    this.postId$ = this.activatedRoute.params.pipe(
      map(params => params['postId']),
      shareReplay(1)
    );
    this.initializePostDetails();
    this.subscribeToAuthStatus();
  }

  subscribeToAuthStatus() {
    this.isAuthenticated$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(status => {
      this.isAuthenticatedStatus = status;
    });
  }

  private initializePostDetails() {
    this.postResponse$ = this.postId$.pipe(
      switchMap(postId => this.postApiService.getById(postId).pipe(
        tap(() => this.incrementViewCount(postId)),
        map(postResponse => postResponse || null),
        catchError(error => {
          this.handleError('Failed to load post details', error);
          return of(null)
        }),
      )),
      shareReplay(1)
    );

    this.safeContent$ = this.postResponse$.pipe(
      map(post => post ? this.sanitizer.bypassSecurityTrustHtml(post.content) : '')
    );
  }

  private incrementViewCount(postId: string) {
    this.postApiService.incrementViewCount(postId).pipe(
      catchError(error => {
        this.handleError('Error incrementing view count', error);
        return of(null);
      }),
      takeUntil(this.destroy$)
    ).subscribe();
  }

  getSafeContent(content: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(content);
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }

  trackByCommentId(index: number, comment: CommentResponse): number {
    return comment.id!;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
