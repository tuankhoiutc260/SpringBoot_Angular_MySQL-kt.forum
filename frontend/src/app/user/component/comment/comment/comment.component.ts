import { Component, ElementRef, EventEmitter, Input, OnInit, OnDestroy, Output, ViewChild, ChangeDetectorRef } from "@angular/core";
import { CommentResponse } from "../../../../api/model/response/comment-response";
import { AuthService } from "../../../../core/service/auth.service";
import { Observable, catchError, throwError, takeUntil, Subject, filter, switchMap, merge, BehaviorSubject, take, map, tap } from "rxjs";
import { PagedResponse } from "../../../../api/model/response/paged-response";
import { CommentApiService } from "../../../../api/service/rest-api/comment-api.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { CommentFormComponent } from "../comment-form/comment-form.component";
import { WebSocketService } from "../../../../api/service/websocket/web-socket.service";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss',
  providers: [MessageService, ConfirmationService],
})
export class CommentComponent implements OnInit, OnDestroy {
  @ViewChild(CommentFormComponent) commentFormComponent!: CommentFormComponent;
  @ViewChild('commentForm') commentForm!: ElementRef;
  @Output() isReplyClicked = new EventEmitter<void>();
  @Output() isSignInClicked = new EventEmitter<void>();
  @Output() pageChangeClicked = new EventEmitter<void>();
  @Input() postId!: string;

  commentResponsePage$!: Observable<PagedResponse<CommentResponse[]>>;
  commentsCurrentPage = 0;
  readonly COMMENTS_PAGE_SIZE: number = 10;
  private destroy$ = new Subject<void>();

  repliedComment: CommentResponse | null = null;
  updateComment: CommentResponse | null = null;

  isAuthenticated$: Observable<boolean>;
  isAuthenticatedStatus = false;

  userLoginId: string | null = null;

  private commentResponsePageSubject = new BehaviorSubject<PagedResponse<CommentResponse[]>>({
    content: [],
    page: { totalElements: 0, totalPages: 0, number: 0, size: this.COMMENTS_PAGE_SIZE }
  });

  constructor(
    private authService: AuthService,
    private commentApiService: CommentApiService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private webSocketService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;

  }

  ngOnInit(): void {
    this.commentResponsePage$ = this.commentResponsePageSubject.asObservable();
    this.userLoginId = this.authService.getCurrentUserId();
    this.initializeComments(this.postId, this.commentsCurrentPage, this.COMMENTS_PAGE_SIZE);
    this.setupWebSocket();
    this.subscribeToAuthStatus()
  }

  private subscribeToAuthStatus() {
    this.isAuthenticated$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(status => {
      this.isAuthenticatedStatus = status;
    });
  }

  private setupWebSocket() {
    this.webSocketService.connect().pipe(
      catchError(error => {
        console.error('WebSocket connection error:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to connect to WebSocket' });
        return throwError(() => new Error('WebSocket connection failed'));
      }),
      filter(connected => connected),
      switchMap(() => this.setupWebSocketSubscriptions(this.postId)),
      takeUntil(this.destroy$)
    ).subscribe();
  }

  private setupWebSocketSubscriptions(postId: string) {
    const newComment$ = this.commentApiService.onNewComment(postId);
    const updateComment$ = this.commentApiService.onUpdateComment(postId);
    const deleteComment$ = this.commentApiService.onDeleteComment(postId);

    return merge(newComment$, updateComment$, deleteComment$).pipe(
      takeUntil(this.destroy$),
      tap(websocketMessage => {
        switch (websocketMessage.type) {
          case 'NEW_COMMENT':
          case 'UPDATE_COMMENT':
            this.websocketAddOrUpdateComment(websocketMessage.payload as CommentResponse);
            break;
          case 'DELETE_COMMENT':
            this.websocketDeleteComment(websocketMessage.payload as number);
            break;
          default:
            console.warn('Unknown WebSocket message type:', websocketMessage.type);
        }
      })
    );
  }

  private websocketAddOrUpdateComment(comment: CommentResponse) {
    this.commentResponsePageSubject.pipe(
      take(1),
      map(currentPage => this.updateCommentPage(currentPage, comment))
    ).subscribe(
      updatedPage => {
        this.commentResponsePageSubject.next(updatedPage);
        this.cdr.detectChanges();
      },
      error => this.handleError('Error updating comments', error)
    );
  }

  private updateCommentPage(currentPage: PagedResponse<CommentResponse[]>, newComment: CommentResponse): PagedResponse<CommentResponse[]> {
    if (!currentPage || !currentPage.content) {
      return {
        content: [newComment],
        page: { totalElements: 1, totalPages: 1, number: 0, size: this.COMMENTS_PAGE_SIZE }
      };
    }

    const existingIndex = currentPage.content.findIndex(c => c.id === newComment.id);
    const updatedComments = existingIndex === -1
      ? [...currentPage.content, newComment]
      : currentPage.content.map(c => c.id === newComment.id ? newComment : c);

    return {
      ...currentPage,
      content: updatedComments,
      page: {
        ...currentPage.page,
        totalElements: currentPage.page.totalElements + (existingIndex === -1 ? 1 : 0)
      }
    };
  }

  private websocketDeleteComment(commentId: number) {
    this.commentResponsePageSubject.pipe(
      take(1),
      map(currentPage => this.removeCommentFromPage(currentPage, commentId))
    ).subscribe(
      updatedPage => {
        this.commentResponsePageSubject.next(updatedPage);
        this.cdr.detectChanges();
        this.initializeComments(this.postId, this.commentsCurrentPage, this.COMMENTS_PAGE_SIZE);
      },
      error => this.handleError('Error deleting comment', error)
    );
  }

  private removeCommentFromPage(currentPage: PagedResponse<CommentResponse[]>, commentId: number): PagedResponse<CommentResponse[]> {
    if (!currentPage || !currentPage.content) {
      return currentPage;
    }

    const updatedComments = currentPage.content.filter(c => c.id !== commentId);
    return {
      ...currentPage,
      content: updatedComments,
      page: {
        ...currentPage.page,
        totalElements: currentPage.page.totalElements - 1
      }
    };
  }

  private initializeComments(postId: string, page: number, size: number) {
    this.commentApiService.getAllCommentAndReplyByPostId(postId, page, size).pipe(
      catchError(error => {
        this.handleError('Failed to load comments', error);
        return throwError(() => new Error(error.message || 'Server error'));
      }),
      takeUntil(this.destroy$)
    ).subscribe(
      initialComments => {
        this.commentResponsePageSubject.next(initialComments);
      },
      error => console.error('Error loading initial comments:', error)
    );
  }

  onReplyComment(commentResponse: CommentResponse) {
    if (this.isAuthenticatedStatus) {
      this.updateComment = null;
      this.repliedComment = commentResponse;
      this.scrollToCommentForm();
    } else {
      this.isReplyClicked.emit();
    }
  }

  onHandleCancelReply() {
    this.repliedComment = null;
    this.updateComment = null;
  }

  onHandleSubmitClicked() {
    this.repliedComment = null;
    this.updateComment = null;
  }

  onUpdateComment(commentResponse: CommentResponse) {
    this.repliedComment = null;
    this.updateComment = commentResponse;
    this.scrollToCommentForm();
  }

  private scrollToCommentForm() {
    this.commentForm.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => {
      this.commentFormComponent.focusOnCommentInput();
    }, 500);
  }

  onDeleteComment(commentId: number, event: Event) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to Delete this Comment?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",

      accept: () => {
        this.commentApiService.deleteById(commentId).pipe(
          catchError(error => {
            this.handleError('Failed to Delete Comment', error);
            return throwError(() => new Error(error.message || 'Server error'));
          }),
          takeUntil(this.destroy$)
        ).subscribe(() =>
          this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Comment deleted' })
        );
      }
    });
  }

  onPageChange(event: any) {
    this.commentsCurrentPage = event.page;
    this.pageChangeClicked.emit();
    this.updateUrlAndFetchComments();
  }

  updateUrlAndFetchComments() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: this.commentsCurrentPage },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.initializeComments(this.postId, this.commentsCurrentPage, this.COMMENTS_PAGE_SIZE);
    });
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
