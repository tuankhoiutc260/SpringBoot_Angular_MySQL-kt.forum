import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { CommentRequest } from '../../../../api/model/request/comment-request';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss']
})
export class CommentsComponent implements OnInit, OnDestroy {
  private newCommentSubscription!: Subscription;

  @Input() postId!: string;

  commentResponseList: CommentResponse[] = [];
  pendingReplies: CommentResponse[] = [];
  currentPage = 0;
  pageSize = 5;
  hasMoreRootComments = true;
  private subscription: Subscription = new Subscription();

  constructor(private commentApiService: CommentApiService) { }

  ngOnInit() {
    this.loadComments();
    this.listenForNewComments();
  }

  loadComments() {
    this.subscription.add(
      this.commentApiService.getCommentsByPostId(this.postId, this.currentPage, this.pageSize).subscribe({
        next: (apiResponse: ApiResponse<CommentResponse[]>) => {
          const newComments = apiResponse.result || [];
          this.commentResponseList = this.mergeRootComments(this.commentResponseList, newComments);
          this.hasMoreRootComments = newComments.length === this.pageSize;
          this.processPendingReplies();
        },
        error: (error) => {
          console.error('Error fetching Comments:', error);
        }
      })
    );
  }
  
  mergeRootComments(existingRootCommentList: CommentResponse[], newRootCommentList: CommentResponse[]): CommentResponse[] {
    const mergedCommentList = [...existingRootCommentList];
    newRootCommentList.forEach(newRootComment => {
      const existingIndex = mergedCommentList.findIndex(c => c.id === newRootComment.id);
      if (existingIndex === -1) {
        mergedCommentList.push(newRootComment);
      } else {
        mergedCommentList[existingIndex] = this.mergeReplies(mergedCommentList[existingIndex], newRootComment);
      }
    });
    return mergedCommentList;
  }

  mergeReplies(existingComment: CommentResponse, newComment: CommentResponse): CommentResponse {
    existingComment.replies = this.mergeRootComments(existingComment.replies || [], newComment.replies || []);
    return existingComment;
  }
 
  loadMoreComments() {
    this.currentPage++;
    this.loadComments();
  }

  listenForNewComments() {
    this.newCommentSubscription = this.commentApiService.onNewComment(this.postId).subscribe(
      newComment => {
        console.log('Received new comment:', newComment);
        if (newComment.parentId === null) {
          this.addOrUpdateComment(newComment);
        } else {
          this.addReplyToComment(newComment);
        }
      }
    );
  }

  addOrUpdateComment(newComment: CommentResponse) {
    const existingIndex = this.commentResponseList.findIndex(c => c.id === newComment.id);
    if (existingIndex === -1) {
      this.commentResponseList.unshift(newComment);
    } else {
      this.commentResponseList[existingIndex] = this.mergeRootComments([this.commentResponseList[existingIndex]], [newComment])[0];
    }
    this.processPendingReplies();
  }

  addReplyToComment(reply: CommentResponse) {
    const success = this.findAndAddReply(this.commentResponseList, reply);
    if (!success) {
      console.log('Parent comment not found for reply, adding to pending replies:', reply);
      this.pendingReplies.push(reply);
    }
  }

  findAndAddReply(comments: CommentResponse[], reply: CommentResponse): boolean {
    for (let comment of comments) {
      reply.parentId = typeof reply.parentId === 'string' ? parseInt(reply.parentId, 10) : reply.parentId;

      if (comment.id === reply.parentId) {
        comment.replies = comment.replies || [];
        const existingReplyIndex = comment.replies.findIndex(r => r.id === reply.id);
        if (existingReplyIndex === -1) {
          comment.replies.push(reply);
        } else {
          comment.replies[existingReplyIndex] = reply;
        }
        return true;
      }
      if (comment.replies && this.findAndAddReply(comment.replies, reply)) {
        return true;
      }
    }
    return false;
  }

  processPendingReplies() {
    const remainingPendingReplies: CommentResponse[] = [];
    for (const reply of this.pendingReplies) {
      const success = this.findAndAddReply(this.commentResponseList, reply);
      if (!success) {
        remainingPendingReplies.push(reply);
      }
    }
    this.pendingReplies = remainingPendingReplies;
  }

  onCommentAdded(comment: CommentRequest) {
    this.commentApiService.addComment({
      ...comment,
      postId: this.postId,
      parentCommentId: null
    }).subscribe({
      next: (response) => {
        if (response.result) {
          this.addOrUpdateComment(response.result);
        }
      },
      error: (error) => {
        console.error('Error adding comment:', error);
      }
    });
  }




  ngOnDestroy() {
    if (this.newCommentSubscription) {
      this.newCommentSubscription.unsubscribe();
    }
  }
}