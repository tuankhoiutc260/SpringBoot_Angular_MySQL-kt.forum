import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommentRequest } from '../../../../api/model/request/comment-request';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { CommentResponse } from '../../../../api/model/response/comment-response';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss']
})
export class CommentFormComponent implements OnChanges {
  // @Input() parentCommentId?: number;
  @Output() commentAdded = new EventEmitter<CommentResponse>();
  @Output() handleCancel = new EventEmitter<void>();

  commentRequest: CommentRequest = {content: '', postId: ''}
  @Input() comment!: CommentResponse;
  @Input() parentCommentId!: number | null
  @Input() postId!: string

  constructor(
    private commentApiService: CommentApiService
  ) {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['postId']) {
      this.commentRequest.postId = this.postId;
    }
  }

  onSubmit() {
    if(this.comment){
      this.commentRequest.postId = this.postId
      if(this.parentCommentId){
        this.commentRequest.parentCommentId = this.parentCommentId
      }
  
    }
     this.commentApiService.addComment(this.commentRequest).subscribe({
      next: (apiResponse: ApiResponse<CommentResponse>) =>{
        this.commentRequest.content = ''
        this.commentAdded.emit(apiResponse.result);

      }
    })
  }
}