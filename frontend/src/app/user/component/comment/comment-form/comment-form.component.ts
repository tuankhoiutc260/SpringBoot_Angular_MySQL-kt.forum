import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { CommentRequest } from '../../../../api/model/request/comment-request';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { catchError, Subject, takeUntil } from 'rxjs';
import { ErrorService } from '../../../../core/service/error.service';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrl: './comment-form.component.scss',
})
export class CommentFormComponent implements OnChanges {
  @ViewChild('commentContent') commentContent!: ElementRef;

  @Output() commentAdded = new EventEmitter<CommentResponse>();
  @Output() commentUpdated = new EventEmitter<CommentResponse>();
  @Output() cancelReply = new EventEmitter<void>();
  @Output() submitClicked = new EventEmitter<void>();

  @Input() postId!: string
  @Input() replyComment: CommentResponse | null = null;
  @Input() updateComment: CommentResponse | null = null;

  commentRequest: CommentRequest = {
    content: '',
    postId: '',
    parentCommentId: null
  }

  private destroy$ = new Subject<void>();

  constructor(
    private commentApiService: CommentApiService,
    private errorService: ErrorService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['postId']) {
      this.commentRequest.postId = this.postId;
    }

    if (changes['updateComment']) {
      this.commentRequest.content = this.updateComment ? this.updateComment.content : '';
    }
  }

  onSubmit() {
    this.commentRequest.parentCommentId = this.replyComment?.id || null;
    this.commentRequest.postId = this.postId;

    const action = this.updateComment ?
      this.commentApiService.update(this.updateComment.id, this.commentRequest) :
      this.commentApiService.create(this.commentRequest);

    action.pipe(
      catchError(this.errorService.handleError('Failed to submit comment')),
      takeUntil(this.destroy$)
    ).subscribe(result => {
      if (result) {
        this.updateComment ? this.commentUpdated.emit(result) : this.commentAdded.emit(result);
        this.resetForm();
        this.submitClicked.emit();
      }
    });
  }

  focusOnCommentInput() {
    this.commentContent.nativeElement.focus();
  }

  onCancelReply() {
    this.resetForm();
    this.cancelReply.emit();
  }

  private resetForm() {
    this.commentRequest.content = '';
    this.replyComment = null;
    this.updateComment = null;
  }
}
