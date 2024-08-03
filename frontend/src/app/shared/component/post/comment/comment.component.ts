import { Component, Input, OnInit } from "@angular/core";
import { CommentResponse } from "../../../../api/model/response/comment-response";
import { CommentApiService } from "../../../../api/service/rest-api/comment-api.service";
import { CommentRequest } from "../../../../api/model/request/comment-request";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
})
export class CommentComponent implements OnInit {
  @Input() comment!: CommentResponse;
  @Input() postId!: string;
  showReplyForm = false;

  constructor(private commentService: CommentApiService) {}

  ngOnInit() {
    if (!this.comment.replies) {
      this.comment.replies = [];
    }
  }

  toggleReplyForm() {
    this.showReplyForm = !this.showReplyForm;
  }

  onReplyAdded(reply: Partial<CommentRequest>) {
    console.log(this.comment.id)
    console.log(this.postId)
    this.commentService.addComment({
      ...reply,
      parentCommentId: this.comment.id,
      postId: this.postId
    }).subscribe({
      next: (response) => {
        if (response.result) {
          // Không cần thêm reply vào đây, vì nó sẽ được xử lý bởi WebSocket
          this.showReplyForm = false;
        }
      },
      error: (error) => {
        console.log(reply)

        console.error('Error adding reply:', error);
      }
    });
  }
}