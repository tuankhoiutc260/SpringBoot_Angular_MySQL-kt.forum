import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject } from 'rxjs';
import { ApiResponse } from '../../model/response/api-response';
import { CommentResponse } from '../../model/response/comment-response';
import { CommentRequest } from '../../model/request/comment-request';
import { Client } from '@stomp/stompjs';
import { environment } from '../../../../enviroments/environment';
import { API_URL } from '../../../core/config/config';

@Injectable({
  providedIn: 'root'
})
export class CommentApiService {
  private apiUrl = API_URL + 'comments';
  private client: Client;
  private isConnected: boolean = false;
  private commentSubject = new Subject<CommentResponse>();

  constructor(private http: HttpClient) {
    this.client = new Client({
      brokerURL: `${environment.wsUrl}`,
      onConnect: () => {
        console.log('Connected to WebSocket');
        this.isConnected = true;
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        this.isConnected = false;
      }
    });
  
    this.client.activate();
  }

  onNewComment(postId: string): Observable<CommentResponse> {
    this.ensureConnection(() => {
      this.client.subscribe(`/topic/comments/${postId}`, message => {
        const comment: CommentResponse = JSON.parse(message.body);
        this.commentSubject.next(comment);
      });
    });
    return this.commentSubject.asObservable();
  }

  private ensureConnection(callback: () => void) {
    if (this.isConnected) {
      callback();
    } else {
      const checkConnection = setInterval(() => {
        if (this.isConnected) {
          clearInterval(checkConnection);
          callback();
        }
      }, 100);
    }
  }

  addComment(commentRequest: CommentRequest): Observable<ApiResponse<CommentResponse>> {
    return this.http.post<ApiResponse<CommentResponse>>(this.apiUrl, commentRequest);
  }

  getCommentsByPostId(postId: string, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/post/${postId}?page=${page}&size=${size}`);
  }

  getRepliesByCommentId(commentId: string, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/${commentId}/replies?page=${page}&size=${size}`);
  }

  getAllRepliesByCommentId(commentId: string, page: number, size: number): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/${commentId}/all-replies?page=${page}&size=${size}`);
  }

  delete(commentId: string): Observable<ApiResponse<void>> {
    const deleteUrl = `${this.apiUrl}/${commentId}`;
    return this.http.delete<ApiResponse<void>>(deleteUrl);
  }
}