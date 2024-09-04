import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { CommentRequest } from '../../../../api/model/request/comment-request';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss']
})
export class CommentFormComponent implements OnChanges {
  @ViewChild('commentContent') commentContent!: ElementRef;

  @Output() commentAdded = new EventEmitter<CommentResponse>();
  @Output() cancelReply = new EventEmitter<void>();

  commentRequest: CommentRequest = {
    content: '', postId: '',
    parentCommentId: null
  }
  @Input() repliedComment!: CommentResponse | null;
  @Input() postId!: string

  constructor(
    private commentApiService: CommentApiService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['postId']) {
      this.commentRequest.postId = this.postId;
    }
  }

  onSubmit() {
    this.commentRequest.parentCommentId = this.repliedComment?.id || null;
    this.commentRequest.postId = this.postId;

    this.commentApiService.create(this.commentRequest).pipe(
      catchError(error => {
        console.error('Failed to create comment:', error);
        return of(null);
      })
    ).subscribe({
      next: (result) => {
        if (result) {
          this.commentAdded.emit();
          this.commentRequest.content = '';
        }
      }
    });
  }

  focusOnCommentInput() {
    this.commentContent.nativeElement.focus();
  }

  onCancelReply() {
    this.repliedComment = null
    this.cancelReply.emit();
  }
}
