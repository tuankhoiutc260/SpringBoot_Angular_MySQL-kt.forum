import { Component, OnInit, OnDestroy, Input, ChangeDetectorRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommentApiService } from '../../../../api/service/rest-api/comment-api.service';
import { CommentResponse } from '../../../../api/model/response/comment-response';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { CommentRequest } from '../../../../api/model/request/comment-request';
import { WebSocketService } from '../../../../api/service/websocket/web-socket.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss']
})
export class CommentsComponent implements OnInit, OnDestroy {
  @Input() postId!: string;

  commentResponseList: CommentResponse[] = [];
  pendingReplies: CommentResponse[] = [];
  currentPage = 0;
  pageSize = 5;
  hasMoreRootComments = true;
  newReply: CommentResponse | null = null;

  private newCommentSubscription!: Subscription;
  private updateCommentSubscription!: Subscription;
  private deleteCommentSubscription!: Subscription;
  private subscription: Subscription = new Subscription();

  constructor(
    private commentApiService: CommentApiService,
    private webSocketService: WebSocketService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadComments();
    this.webSocketService.connect().subscribe(
      connected => {
        if (connected) {
          this.listenForNewComments();
          this.listenForUpdatedComments();
          this.listenForDeletedComments();
        }
      }
    );
  }

  loadComments() {
    this.subscription.add(
      this.commentApiService.getCommentsByPostId(this.postId, this.currentPage, this.pageSize).subscribe({
        next: (apiResponse: ApiResponse<CommentResponse[]>) => {
          const newCommentList = apiResponse.result || [];
          this.commentResponseList = this.mergeRootComments(this.commentResponseList, newCommentList);
          this.hasMoreRootComments = newCommentList.length === this.pageSize;
          this.processPendingReplies();
          this.cdr.detectChanges();
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
      const existingIndex = mergedCommentList.findIndex(comment => comment.id === newRootComment.id);
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
      websocketMessage => {
        const newComment = websocketMessage.payload;
        console.log('Received new comment:', newComment);
        if (newComment.parentId === null) {
          this.addOrUpdateComment(newComment);
        } else {
          this.addReplyToComment(newComment);
          this.newReply = newComment;
        }
        this.processPendingReplies();
        this.cdr.detectChanges();
      }
    );
  }

  listenForUpdatedComments() {
    this.updateCommentSubscription = this.commentApiService.onUpdateComment(this.postId).subscribe(
      websocketMessage => {
        const updatedComment = websocketMessage.payload;
        console.log('Received updated comment:', updatedComment);
        this.updateCommentInList(updatedComment);
        this.cdr.detectChanges();
      }
    );
  }

  listenForDeletedComments() {
    this.deleteCommentSubscription = this.commentApiService.onDeleteComment(this.postId).subscribe(
      websocketMessage => {
        const deletedCommentId = websocketMessage.payload;
        console.log('Received deleted comment ID:', deletedCommentId);
        this.removeCommentFromList(deletedCommentId);
        this.cdr.detectChanges();
      }
    );
  }

  addOrUpdateComment(newComment: CommentResponse) {
    const existingIndex = this.commentResponseList.findIndex(comment => comment.id === newComment.id);
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
          comment.replies.unshift(reply);
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
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error adding comment:', error);
      }
    });
  }

  updateCommentInList(updatedComment: CommentResponse) {
    const updateComment = (comments: CommentResponse[]): boolean => {
      for (let i = 0; i < comments.length; i++) {
        if (comments[i].id === updatedComment.id) {
          comments[i] = { ...comments[i], ...updatedComment };
          return true;
        }
        if (comments[i].replies) {
          const updatedInReplies = updateComment(comments[i].replies!);
          if (updatedInReplies) return true;
        }
      }
      return false;
    };
  
    updateComment(this.commentResponseList);
  }

  removeCommentFromList(commentId: number) {
    const removeComment = (comments: CommentResponse[]): boolean => {
      for (let i = 0; i < comments.length; i++) {
        if (comments[i].id === commentId) {
          comments.splice(i, 1);
          return true;
        }
        if (comments[i].replies) {
          const removedFromReplies = removeComment(comments[i].replies!);
          if (removedFromReplies) return true;
        }
      }
      return false;
    };
  
    removeComment(this.commentResponseList);
  }

  ngOnDestroy() {
    if (this.newCommentSubscription) {
      this.newCommentSubscription.unsubscribe();
    }
    if (this.updateCommentSubscription) {
      this.updateCommentSubscription.unsubscribe();
    }
    if (this.deleteCommentSubscription) {
      this.deleteCommentSubscription.unsubscribe();
    }
    this.subscription.unsubscribe();
  }
}