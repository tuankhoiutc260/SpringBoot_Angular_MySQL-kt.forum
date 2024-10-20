import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ApiResponse } from '../../model/response/api-response';
import { CommentResponse } from '../../model/response/comment-response';
import { CommentRequest } from '../../model/request/comment-request';
import { environment } from '../../../../environments/environment';
import { WebSocketService } from '../websocket/web-socket.service';
import { WebSocketMessage } from '../../model/entity/web-socket-message';
import { PagedResponse } from '../../model/response/paged-response';

@Injectable({
  providedIn: 'root'
})
export class CommentApiService {
  private readonly apiUrl = `${environment.apiUrl}/comments`;

  constructor(
    private http: HttpClient,
    private webSocketService: WebSocketService
  ) { }

  onNewComment(postId: string): Observable<WebSocketMessage<CommentResponse>> {
    return this.webSocketService.listen(`/topic/comments/${postId}`);
  }

  onUpdateComment(postId: string): Observable<WebSocketMessage<CommentResponse>> {
    return this.webSocketService.listen(`/topic/comments/${postId}/update`);
  }

  onDeleteComment(postId: string): Observable<WebSocketMessage<number>> {
    return this.webSocketService.listen(`/topic/comments/${postId}/delete`);
  }

  create(commentRequest: CommentRequest): Observable<CommentResponse> {
    return this.http.post<ApiResponse<CommentResponse>>(this.apiUrl, commentRequest, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(commentId: number): Observable<CommentResponse> {
    return this.http.get<ApiResponse<CommentResponse>>(`${this.apiUrl}/id/${commentId}`, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAllCommentAndReplyByPostId(postId: string, page: number, size: number): Observable<PagedResponse<CommentResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<CommentResponse[]>>>(`${this.apiUrl}/post/${postId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getRepliesByCommentId(commentId: number, page: number, size: number): Observable<PagedResponse<CommentResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<CommentResponse[]>>>(`${this.apiUrl}/${commentId}/replies`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(commentId: number, commentRequest: CommentRequest): Observable<CommentResponse> {
    return this.http.put<ApiResponse<CommentResponse>>(`${this.apiUrl}/${commentId}`, commentRequest, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(commentId: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${commentId}`, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Lỗi phía client
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      // Lỗi phía server
      errorMessage = `Server-side error: ${error.status} ${error.message}`;
    }
    console.error('Error occurred:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
