import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, Subject } from 'rxjs';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

import { ApiResponse } from '../../model/response/api-response';
import { CommentResponse } from '../../model/response/comment-response';
import { CommentRequest } from '../../model/request/comment-request';
import { Client } from '@stomp/stompjs';
import { environment } from '../../../../enviroments/environment';
import { WebSocketService } from '../websocket/web-socket.service';
import { WebSocketMessage } from '../../model/entity/web-socket-message';

@Injectable({
  providedIn: 'root'
})
export class CommentApiService {
  private apiUrl = `${environment.apiUrl}/comments`;
  constructor(private http: HttpClient,
    private webSocketService: WebSocketService
  ) {
  }

  onNewComment(postId: string): Observable<WebSocketMessage<CommentResponse>> {
    return this.webSocketService.listen(`/topic/comments/${postId}`);
  }

  onUpdateComment(postId: string): Observable<WebSocketMessage<CommentResponse>> {
    return this.webSocketService.listen(`/topic/comments/${postId}/update`);
  }

  onDeleteComment(postId: string): Observable<WebSocketMessage<number>> {
    return this.webSocketService.listen(`/topic/comments/${postId}/delete`);
  }

  // onNewComment(postId: string): Observable<CommentResponse> {
  //   return new Observable(observer => {
  //     this.socket$.subscribe(
  //       message => {
  //         if (message.type === 'NEW_COMMENT' && message.postId === postId) {
  //           observer.next(message.comment);
  //         }
  //       },
  //       error => observer.error(error),
  //       () => observer.complete()
  //     );
  //   });
  // }

  addComment(commentRequest: CommentRequest): Observable<ApiResponse<CommentResponse>> {
    return this.http.post<ApiResponse<CommentResponse>>(this.apiUrl, commentRequest);
  }

  getCommentsByPostId(postId: string, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/post/${postId}?page=${page}&size=${size}`);
  }

  getRepliesByCommentId(commentId: number, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/${commentId}/replies?page=${page}&size=${size}`);
  }

  getAllRepliesByCommentId(commentId: number, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/${commentId}/all-replies?page=${page}&size=${size}`);
  }

  updateComment(commentId: number, comment: CommentRequest): Observable<ApiResponse<CommentResponse>> {
    return this.http.put<ApiResponse<CommentResponse>>(`${this.apiUrl}/${commentId}`, comment);
  }

  delete(commentId: number): Observable<ApiResponse<void>> {
    const deleteUrl = `${this.apiUrl}/${commentId}`;
    return this.http.delete<ApiResponse<void>>(deleteUrl);
  }
}