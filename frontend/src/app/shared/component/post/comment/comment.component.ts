import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from "@angular/core";
import { CommentResponse } from "../../../../api/model/response/comment-response";
import { CommentApiService } from "../../../../api/service/rest-api/comment-api.service";
import { CommentRequest } from "../../../../api/model/request/comment-request";
import { ConfirmationService, MessageService } from "primeng/api";
import { AuthService } from "../../../../core/service/auth.service";
import { Subscription } from "rxjs";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
  providers: [MessageService, ConfirmationService]
})
export class CommentComponent implements OnInit, OnChanges{
  @Input() comment!: CommentResponse;
  @Input() postId!: string;
  @Output() commentDeleted = new EventEmitter<number>();
  @Output() commentUpdated = new EventEmitter<CommentResponse>();

  showReplyForm = false;
  showReplies = false;
  displayedReplies: CommentResponse[] = [];
  hasMoreReplies = false;
  private repliesPerPage = 5;
  private currentReplyPage = 0;
  isEditing = false;
  editedContent = '';
  canEdit: boolean = false;
  private subscription: Subscription = new Subscription();

  constructor(
    private commentService: CommentApiService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private authService: AuthService

  ) {}

  ngOnInit() {
    if (!this.comment.replies) {
      this.comment.replies = [];
    }
    this.loadInitialReplies();
    this.updateDisplayedReplies();
    
    this.comment.createdBy == this.authService.getUserID() ? this.canEdit = true : this.canEdit = false

  }

  toggleReplies() {
    this.showReplies = !this.showReplies;

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["comment"]) {
      this.updateDisplayedReplies();
    }
  }
  
  private updateDisplayedReplies(): void {
    this.displayedReplies = this.comment.replies || [];
  }

  // getUserLoginInfo() {
  // }
  

  check(){
    console.log(this.displayedReplies)
    console.log(this.comment)
  }

  loadInitialReplies() {
    if (this.comment.replies && this.comment.replies.length > 0) {
      this.displayedReplies = this.comment.replies.slice(0, this.repliesPerPage);
      this.hasMoreReplies = this.comment.replies.length > this.repliesPerPage;
    } else {
      this.displayedReplies = [];
      this.hasMoreReplies = false;
    }
    this.currentReplyPage = 0;
  }

  loadMoreReplies() {
    this.currentReplyPage++;
    const startIndex = this.currentReplyPage * this.repliesPerPage;
    const endIndex = startIndex + this.repliesPerPage;
    const newReplies = this.comment.replies!.slice(startIndex, endIndex);
    this.displayedReplies = [...this.displayedReplies, ...newReplies];
    this.hasMoreReplies = endIndex < this.comment.replies!.length;
  }

  toggleReplyForm() {
    this.showReplyForm = !this.showReplyForm;
  }

onReplyAdded(reply: Partial<CommentRequest>) {
  this.commentService.addComment({
    ...reply,
    parentCommentId: this.comment.id,
    postId: this.postId
  }).subscribe({
    next: (response) => {
      if (response.result) {
        this.showReplyForm = false;
        this.loadInitialReplies();
        this.showReplies = true;
      }
    },
    error: (error) => {
      console.error('Error adding reply:', error);
      this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to add reply'});
    }
  });
}

  onDeleteComment() {
    this.confirmationService.confirm({
      message: 'Do you want to delete this comment?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",
      accept: () => {
        this.commentService.delete(this.comment.id!).subscribe({
          next: () => {
            this.commentDeleted.emit(this.comment.id);
            this.messageService.add({severity:'success', summary: 'Success', detail: 'Comment deleted'});
          },
          error: (error) => {
            console.error('Error deleting comment:', error);
            this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to delete comment'});
          }
        });
      }
    });
  }

  startEditing() {
    this.isEditing = true;
    this.editedContent = this.comment.content!;
  }

  cancelEditing() {
    this.isEditing = false;
    this.editedContent = '';
  }

  saveEdit() {
    if (this.editedContent.trim() !== this.comment.content!.trim()) {
      const updatedComment: CommentRequest = {
        content: this.editedContent,
        postId: this.postId,
        parentCommentId: this.comment.parentId
      };

      this.commentService.updateComment(this.comment.id!, updatedComment).subscribe({
        next: (response) => {
          if (response.result) {
            this.comment.content = response.result.content;
            this.commentUpdated.emit(response.result);
            this.isEditing = false;
            this.messageService.add({severity:'success', summary: 'Success', detail: 'Comment updated'});
          }
        },
        error: (error) => {
          console.error('Error updating comment:', error);
          this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to update comment'});
        }
      });
    } else {
      this.isEditing = false;
    }
  }

  
}