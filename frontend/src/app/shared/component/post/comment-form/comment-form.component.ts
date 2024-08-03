import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentRequest } from '../../../../api/model/request/comment-request';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss']
})
export class CommentFormComponent {
  @Input() parentCommentId?: number;
  @Output() commentAdded = new EventEmitter<CommentRequest>();
  @Output() handleCancel = new EventEmitter<void>();

  commentRequest: CommentRequest = { content: '' };

  onSubmit() {
    if (this.commentRequest.content && this.commentRequest.content.trim().length > 0) {
      this.commentAdded.emit(this.commentRequest);
      this.commentRequest = { content: '' };
    }
  }
}