import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { CommentService } from '../../../../api/service/comment.service';
import { WebSocketService } from '../../../../api/service/web-socket.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ApiResponse } from '../../../../api/model/response/apiResponse';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { CommentRequest } from '../../../../api/model/request/comment-request';

@Component({
  selector: 'app-comments',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss',
})
export class CommentsComponent implements OnInit, OnDestroy {
  private wsSubscription: Subscription = new Subscription();
  private httpSubscription: Subscription = new Subscription();
  @Input() postID!: string;

  constructor(
    private commentService: CommentService,
    private webSocketService: WebSocketService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.loadComments();
      this.subscribeToComments();
    });
  }

  private subscribeToComments() {
      this.wsSubscription.unsubscribe();
      this.wsSubscription  = this.webSocketService.getCommentUpdates(this.postID).subscribe(
      (message) => {
        if (message && message.type === 'NEW_COMMENT') {
          this.commentResponseList.unshift(message.payload);
        } else if (message && message.type === 'DELETE_COMMENT') {
          this.commentResponseList = this.commentResponseList.filter(c => c.id !== message.payload);
        }
      }
    );
  }

  commentResponseList: CommentResponse[] = []
  commentRequest: CommentRequest = {}
  commentResponse: CommentResponse = {}
  loadComments() {
    const sub = this.commentService.findByByPostID(this.postID).subscribe({
      next: (apiResponse: ApiResponse<CommentResponse[]>) => {
        const commentReponseResultList = apiResponse.result;
        if (commentReponseResultList) {
          this.commentResponseList = commentReponseResultList;
          console.log("Load Comment 1", commentReponseResultList);
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching Comments:', error);
      }
    });
    this.httpSubscription.add(sub);
  }

  addComment() {
    this.commentRequest.postID = this.postID;
    this.httpSubscription.add(
      this.commentService.create(this.commentRequest).subscribe({
        next: (apiResponse: ApiResponse<CommentResponse>) => {
          const commentResponse = apiResponse.result;
          if (commentResponse) {
            this.commentResponse = commentResponse;
            this.commentRequest = {}
          } else {
            console.error('No result found in response:', apiResponse.message);
          }
        },
        error: (error) => {
          console.error('Error adding Comment:', error);
        }
      })
    );
  }

  ngOnDestroy() {
    this.httpSubscription.unsubscribe();
  }
}