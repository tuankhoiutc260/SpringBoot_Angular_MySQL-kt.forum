import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PostRequest } from '../../model/request/post-request';
import { ApiResponse } from '../../model/response/api-response';
import { PostResponse } from '../../model/response/post-response';
import { environment } from '../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostApiService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) { }

  create(postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    const formData = new FormData();
    if (postRequest.image) {
      formData.append('image', postRequest.image);
    }
    formData.append('title', postRequest.title!);
    formData.append('content', postRequest.content!);
    formData.append('tags', postRequest.tags!.join(','));
    return this.http.post<ApiResponse<PostResponse>>(this.apiUrl, formData);
  }

  findById(postRequestId: string): Observable<ApiResponse<PostResponse>> {
    const url = `${this.apiUrl}/id/${postRequestId}`;
    return this.http.get<ApiResponse<PostResponse>>(url);
  }

  findByCreatedBy(userId: string): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiUrl}/user-post/${userId}`;
    return this.http.get<ApiResponse<PostResponse[]>>(url);
  }


  findTop10ByOrderByLikesDesc(): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiUrl}/top10`;
    return this.http.get<ApiResponse<PostResponse[]>>(url);
  }

  findPostsLiked(userId: string): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiUrl}/posts-liked/${userId}`;;
    return this.http.get<ApiResponse<PostResponse[]>>(url);
  }

  findAll(): Observable<ApiResponse<PostResponse[]>> {
    return this.http.get<ApiResponse<PostResponse[]>>(this.apiUrl);
  }

  update(postRequestId: string, postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    const formData = new FormData();
    if (postRequest.image) {
      formData.append('image', postRequest.image);
    }
    formData.append('title', postRequest.title!);
    formData.append('content', postRequest.content!);
    formData.append('tags', postRequest.tags!.join(','));
    const url = `${this.apiUrl}/${postRequestId}`;
    return this.http.put<ApiResponse<PostResponse>>(url, formData);
  }

  delete(postRequestId: string): Observable<ApiResponse<void>> {
    const url = `${this.apiUrl}/${postRequestId}`;
    return this.http.delete<ApiResponse<void>>(url);
  }

  save(postRequestId: string | null, postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    if (postRequestId) {
      return this.update(postRequestId, postRequest);
    } else {
      return this.create(postRequest);
    }
  }
}