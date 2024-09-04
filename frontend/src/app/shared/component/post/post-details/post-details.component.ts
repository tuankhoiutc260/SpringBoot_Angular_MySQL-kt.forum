import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Observable, Subject, BehaviorSubject, of, merge } from 'rxjs';
import { takeUntil, map, catchError, switchMap, tap, shareReplay, filter, take } from 'rxjs/operators';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { WebSocketService } from '../../../../api/service/websocket/web-socket.service';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { CommentFormComponent } from '../comment-form/comment-form.component';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'],
  providers: [MessageService, ConfirmationService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailsComponent implements OnInit, OnDestroy {
  @ViewChild(CommentFormComponent) commentFormComponent!: CommentFormComponent;
  @ViewChild('commentForm') commentForm!: ElementRef;

  postId$!: Observable<string>;
  postResponse$!: Observable<PostResponse | null>;
  safeContent$!: Observable<SafeHtml>;
  minRead$!: Observable<number>;
  commentResponsePage$!: Observable<PagedResponse<CommentResponse[]>>;

  readonly COMMENTS_PAGE_SIZE: number = 10;
  commentsCurrentPage = 0;

  repliedComment: CommentResponse | null = null;

  private destroy$ = new Subject<void>();
  private commentResponsePageSubject = new BehaviorSubject<PagedResponse<CommentResponse[]>>({
    content: [],
    page: { totalElements: 0, totalPages: 0, number: 0, size: this.COMMENTS_PAGE_SIZE }
  });

  constructor(
    private postApiService: PostApiService,
    private activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private commentApiService: CommentApiService,
    private webSocketService: WebSocketService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
  ) { }

  ngOnInit() {
    this.postId$ = this.activatedRoute.params.pipe(
      map(params => params['postId']),
      shareReplay(1)
    );

    this.initializePostDetails();
    this.initializeComments();
    this.setupWebSocket();
  }

  private initializePostDetails() {
    this.postResponse$ = this.postId$.pipe(
      switchMap(postId => this.postApiService.getById(postId).pipe(
        tap(() => this.incrementViewCount(postId)),
        map(postResponse => postResponse || null),
        catchError(error => {
          console.error('Error fetching Post:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load post details' });
          return of(null);
        })
      )),
      shareReplay(1)
    );

    this.safeContent$ = this.postResponse$.pipe(
      map(post => post ? this.sanitizer.bypassSecurityTrustHtml(post.content) : '')
    );

    this.minRead$ = this.postResponse$.pipe(
      map(post => post ? this.calculateMinRead(post.content) : 0)
    );
  }

  private incrementViewCount(postId: string) {
    this.postApiService.incrementViewCount(postId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      error: (error) => console.error('Error incrementing view count:', error)
    });
  }

  private calculateMinRead(content: string): number {
    const wordsPerMinute = 200;
    const cleanText = content.replace(/[^\w\s]/gi, '');
    const textLength = cleanText.split(/\s+/).length;
    return Math.ceil(textLength / wordsPerMinute);
  }

  private initializeComments() {
    this.commentResponsePage$ = this.commentResponsePageSubject.asObservable();
    this.loadComments();
  }

  loadComments() {
    this.postId$.pipe(
      switchMap(postId => this.commentApiService.getAllCommentAndReplyByPostId(postId, this.commentsCurrentPage, this.COMMENTS_PAGE_SIZE)
        .pipe(
          catchError(error => {
            console.error(`Error fetching Comment with Post Id: ${postId}: `, error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load comments' });
            return of({
              content: [],
              page: { totalElements: 0, totalPages: 0, number: 0, size: this.COMMENTS_PAGE_SIZE }
            });
          }),
        )),
      takeUntil(this.destroy$)
    ).subscribe(commentResponsePage => {
      this.commentResponsePageSubject.next(commentResponsePage);
    });
  }

  private setupWebSocket() {
    this.webSocketService.connect().pipe(
      filter(connected => connected),
      switchMap(() => this.postId$),
      takeUntil(this.destroy$)
    ).subscribe(
      postId => this.setupWebSocketSubscriptions(postId)
    );
  }

  private setupWebSocketSubscriptions(postId: string) {
    const newComment$ = this.commentApiService.onNewComment(postId);
    const updateComment$ = this.commentApiService.onUpdateComment(postId);
    const deleteComment$ = this.commentApiService.onDeleteComment(postId);

    merge(newComment$, updateComment$, deleteComment$)
      .pipe(takeUntil(this.destroy$))
      .subscribe(websocketMessage => {
        switch (websocketMessage.type) {
          case 'NEW_COMMENT':
          case 'UPDATE_COMMENT':
            this.websocketAddOrUpdateComment(websocketMessage.payload as CommentResponse);
            break;
          case 'DELETE_COMMENT':
            this.websocketRemoveComment(websocketMessage.payload as number);
            break;
        }
      });
  }

  websocketAddOrUpdateComment(comment: CommentResponse) {
    this.commentResponsePageSubject.pipe(
      take(1),
      map(currentCommentResponsePage => {
        if (!currentCommentResponsePage || !currentCommentResponsePage.content) {
          return {
            content: [comment],
            page: { totalElements: 1, totalPages: 1, number: 0, size: this.COMMENTS_PAGE_SIZE }
          };
        }
        const existingIndex = currentCommentResponsePage.content.findIndex(c => c.id === comment.id);
        const updatedComment = existingIndex === -1
          ? [...currentCommentResponsePage.content, comment]
          : currentCommentResponsePage.content.map(c => c.id === comment.id ? comment : c);

        return {
          ...currentCommentResponsePage,
          content: updatedComment,
          page: {
            ...currentCommentResponsePage.page,
            totalElements: currentCommentResponsePage.page.totalElements + (existingIndex === -1 ? 1 : 0)
          }
        };
      })
    ).subscribe(updatedPage => {
      this.commentResponsePageSubject.next(updatedPage);
    });
  }

  websocketRemoveComment(commentId: number) {
    this.commentResponsePageSubject.pipe(
      take(1),
      map(currentCommentResponsePage => {
        if (!currentCommentResponsePage || !currentCommentResponsePage.content) {
          return currentCommentResponsePage;
        }
        return {
          ...currentCommentResponsePage,
          content: currentCommentResponsePage.content.filter(comment => comment.id !== commentId),
          page: {
            ...currentCommentResponsePage.page,
            totalElements: currentCommentResponsePage.page.totalElements - 1
          }
        };
      })
    ).subscribe(updatedPage => {
      this.commentResponsePageSubject.next(updatedPage);
    });
  }

  onDeleteComment(commentId: number) {
    this.confirmationService.confirm({
      message: 'Do you want to delete this comment?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",
      accept: () => {
        this.commentApiService.deleteById(commentId)
          .pipe(
            takeUntil(this.destroy$)
          ).subscribe({
            next: () => {
              this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Comment deleted' });
              this.loadComments(); 
            },
            error: (error) => {
              console.error('Failed to delete comment:', error);
              this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete comment' });
            }
          });
      }
    });
  }

  handleReplyComment(commentResponse: CommentResponse) {
    this.repliedComment = commentResponse;
    this.commentForm.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => {
      this.commentFormComponent.focusOnCommentInput();
    }, 500);
  }

  handleCancelReply() {
    this.repliedComment = null;
  }

  handleCommentAdded() {
    this.repliedComment = null;
  }

  onPageChange(event: any) {
    this.commentsCurrentPage = event.page;
    this.updateUrlAndFetchComments();
  }

  updateUrlAndFetchComments() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: this.commentsCurrentPage },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.loadComments();
    });
  }

  trackByCommentId(index: number, comment: CommentResponse): number {
    return comment.id!;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
