import { Component, EventEmitter, Input, Output } from "@angular/core";
import { CommentResponse } from "../../../../api/model/response/comment-response";
import { ConfirmationService, MessageService } from "primeng/api";
import { PostResponse } from "../../../../api/model/response/post-response";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
  providers: [MessageService, ConfirmationService]
})

export class CommentComponent {
  @Input() commentResponse!: CommentResponse;
  @Input() postOfComment!: PostResponse;

  @Output() showReplyForm = new EventEmitter<boolean>();

  @Input() isReplyFormVisible: boolean = false;
  
  toggleReplyForm() {
    this.showReplyForm.emit(!this.isReplyFormVisible);
  }
}