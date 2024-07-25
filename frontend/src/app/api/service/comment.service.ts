import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../model/response/apiResponse';
import { CommentResponse } from '../model/response/comment-response';
import { API_URL } from '../../core/config/config';
import { CommentRequest } from '../model/request/comment-request';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = API_URL + 'comments';

  constructor(private http: HttpClient) { }

  findByByPostID(postID: string): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(`${this.apiUrl}/post/${postID}`);
  }

  create(commentRequest: CommentRequest): Observable<ApiResponse<CommentResponse>> {
    return this.http.post<ApiResponse<CommentResponse>>(this.apiUrl, commentRequest);
  }

  delete(commentID: string): Observable<ApiResponse<void>> {
    const deleteUrl = `${this.apiUrl}/${commentID}`;
    return this.http.delete<ApiResponse<void>>(deleteUrl)
  }
}