import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { CommentResponse } from "../../../../api/model/response/comment-response";
import { PostResponse } from "../../../../api/model/response/post-response";
import { AuthService } from "../../../../core/service/auth.service";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
})

export class CommentComponent implements OnInit {
  @Input() commentResponse!: CommentResponse;
  @Input() postOfComment!: PostResponse;

  @Output() reply = new EventEmitter<CommentResponse>();
  @Output() delete = new EventEmitter<void>();

  userLoginId: string | null = null;

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.userLoginId = this.authService.getUserID()
  }

  onReplyComment(commentResponse: CommentResponse) {
    this.reply.emit(commentResponse);
  }

  onDeleteComment() {
    this.delete.emit();
  }
}
