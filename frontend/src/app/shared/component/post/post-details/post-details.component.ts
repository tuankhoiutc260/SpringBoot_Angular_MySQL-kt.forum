import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, Subject, BehaviorSubject, Subscription, of } from 'rxjs';
import { takeUntil, map, catchError } from 'rxjs/operators';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { WebSocketService } from '../../../../api/service/websocket/web-socket.service';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'],
  providers: [MessageService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PostDetailsComponent implements OnInit, OnDestroy {
  postId: string = '';
  minRead: number = 0;
  commentsCurrentPage = 0;
  commentsPageSize = 10;
  totalComments = 0;
  postResponse$!: Observable<PostResponse | null>;
  safeContent$!: Observable<SafeHtml>;
  private commentResponseListSubject = new BehaviorSubject<CommentResponse[]>([]);
  commentResponseList$ = this.commentResponseListSubject.asObservable();
  replyingToCommentId: number | null = null;

  private destroy$ = new Subject<void>();
  private newCommentSubscription: Subscription | null = null;
  private updateCommentSubscription: Subscription | null = null;
  private deleteCommentSubscription: Subscription | null = null;

  constructor(
    private postApiService: PostApiService,
    private activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private commentApiService: CommentApiService,
    private webSocketService: WebSocketService,
    private router: Router,

  ) { }

  ngOnInit() {
    this.activatedRoute.params.pipe(
      takeUntil(this.destroy$)
    ).subscribe(params => {
      this.postId = params['postId'];
      this.getPostDetails();
      this.loadComments();
      this.incrementViewCount();  // Gọi hàm để tăng số lần xem

    });

    this.webSocketService.connect().subscribe(
      connected => {
        if (connected) {
          this.setupWebSocketSubscriptions();
        }
      }
    );
  }

  private setupWebSocketSubscriptions() {
    this.newCommentSubscription = this.commentApiService.onNewComment(this.postId).subscribe(
      websocketMessage => {
        const newComment = websocketMessage.payload;
        this.addOrUpdateComment(newComment);
      }
    );

    this.updateCommentSubscription = this.commentApiService.onUpdateComment(this.postId).subscribe(
      websocketMessage => {
        const updatedComment = websocketMessage.payload;
        this.addOrUpdateComment(updatedComment);
      }
    );

    this.deleteCommentSubscription = this.commentApiService.onDeleteComment(this.postId).subscribe(
      websocketMessage => {
        const deletedCommentId = websocketMessage.payload;
        this.removeComment(deletedCommentId);
      }
    );
  }

  getPostDetails() {
    this.postResponse$ = this.postApiService.findById(this.postId).pipe(
      map(apiResponse => apiResponse.result || null),
      catchError(error => {
        console.error('Error fetching Post:', error);
        return of(null)
      })
    );

    this.safeContent$ = this.postResponse$.pipe(
      map(post => post ? this.sanitizer.bypassSecurityTrustHtml(post.content) : '')
    );

    this.postResponse$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(post => {
      if (post) {
        this.minRead = this.calculateMinRead(post.content);
      }
    });
  }

  loadComments() {
    this.commentApiService.getCommentsByPostId(this.postId, this.commentsCurrentPage, this.commentsPageSize).pipe(
      map(apiResponse => apiResponse.result || []),
      catchError(error => {
        console.error(`Error fetching Comment with Post Id: ${this.postId}: `, error);
        return [];
      })
    ).subscribe(comments => {
      this.commentResponseListSubject.next(comments);
    });
  }

  // onPageChange(event: any) {
  //   this.commentsCurrentPage = event.page;
  //   this.commentsPageSize = event.rows;
  //   this.loadComments();
  // }

  onPageChange(event: any) {
    this.commentsCurrentPage = event.page + 1;
    this.updateUrlAndFetchComments();

  }

  private updateUrlAndFetchComments() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: this.commentsCurrentPage },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.loadComments();
    });
  }

  addOrUpdateComment(newComment: CommentResponse) {
    const currentComments = this.commentResponseListSubject.getValue();
    const existingIndex = currentComments.findIndex(comment => comment.id === newComment.id);
    if (existingIndex === -1) {
      this.commentResponseListSubject.next([newComment, ...currentComments]);
    } else {
      currentComments[existingIndex] = newComment;
      this.commentResponseListSubject.next([...currentComments]);
    }
  }

  removeComment(commentId: number) {
    const currentComments = this.commentResponseListSubject.getValue();
    this.commentResponseListSubject.next(currentComments.filter(comment => comment.id !== commentId));
  }

  calculateMinRead(text: string): number {
    const wordsPerMinute = 200;
    const cleanText = text.replace(/[^\w\s]/gi, '');
    const textLength = cleanText.split(/\s+/).length;
    return Math.ceil(textLength / wordsPerMinute);
  }

  handleReplyFormVisibility(isVisible: boolean, commentId: number) {
    this.replyingToCommentId = isVisible ? commentId : null;
  }

  isReplyFormVisible(commentId: number): boolean {
    return this.replyingToCommentId === commentId;
  }

  onCommentAdded(newComment: CommentResponse) {
    // this.addOrUpdateComment(newComment);
    this.replyingToCommentId = null; // Ẩn form sau khi thêm bình luận
  }

  trackByCommentId(index: number, comment: CommentResponse): number {
    return comment.id!;
  }

  incrementViewCount() {
    this.postApiService.incrementViewCount(this.postId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => console.log('View count incremented'),
      error: (error) => console.error('Error incrementing view count:', error)
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();

    if (this.newCommentSubscription) {
      this.newCommentSubscription.unsubscribe();
    }
    if (this.updateCommentSubscription) {
      this.updateCommentSubscription.unsubscribe();
    }
    if (this.deleteCommentSubscription) {
      this.deleteCommentSubscription.unsubscribe();
    }
  }
}